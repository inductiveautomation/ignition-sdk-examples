/**
 * Example of a component that utilizes the ComponentStoreDelegate api to send and receive payload-containing messages
 * between the browser's instance of a component/store and the gateway's model.  In addition, this examples utilizes
 * 'mobx' as a means of creating/storing component state that is independent from the Perspective's PropertyTree.
 *
 * This is a rather silly example that demonstrates a few different concepts:
 *
 *  1. The use of an 'unmanaged' component store (store here meaning 'place where a stateful component's state is
 * held').  Such a pattern is common in the case of large, complex components that might have internal state that is
 * not necessary/useful if presented to users as part of the component's configuration.  Such state can be held any way
 * that's convenient to you, the component author.  React's `setState`, React Context, Redux, or (as in this example)
 * mobx, are just a few of the many possible options for keeping component-local state.
 *
 * 2. The Client-Side implementation of a ComponentStoreDelegate. The Delegate api provides a convenient way to send
 * messages over the websocket established by the Perspective runtime.  A mirror API on the gateway provides a
 * corresponding opportunity to send/receive messages from the gateway.  Together, they provide a seamless 'realtime'
 * message channel for sending state to/from your client stores and server models.
 *
 * For clarity, these two concepts have been broken out into their own classes: CustomValueStore and
 * MessageComponentGatewayDelegate.
 *
 *
 */

import { observable, action, reaction, IReactionDisposer } from 'mobx';
import { observer } from 'mobx-react';
import * as React from 'react';
import {
    AbstractUIElementStore,
    Component,
    ComponentMeta,
    ComponentProps,
    ComponentStoreDelegate,
    isFunction,
    JsObject,
    makeLogger, PComponent,
    PropertyTree,
    SizeObject
} from '@inductiveautomation/perspective-client';
import { bind } from 'bind-decorator';


// the 'key' or 'id' for this component type.  Component must be registered with this EXACT key in the Java side as well
// as on the client side.  In the client, this is done in the index file where we import and register through the
// ComponentRegistry provided by the perspective-client API.
export const COMPONENT_TYPE = "rad.display.messenger";

/**
 * Name of the message config prop defined in the json schema defined in common/src/main/resources/messenger.props.json
 */
export const MESSAGE_CONFIG_PROP = "messageConfig";

interface MessagePropConfig {
    [key: string]: string;
}


// default configuration in component props.  Added here just as a useful reference.  Generally only defined in
// the prop schema (props.json file).
export const DEFAULT_MESSAGE_CONFIG: MessagePropConfig = {
    "0": "None",
    "1": "Messages!",
    "5": "Lots of Messages!",
    "10": "Literally ten+ messages!",
    "25": "Carpal Tunnel Warning!"
};

export interface MessageResponseHandler {
    handleMessageResponse(payload: JsObject): void;
}

const logger = makeLogger("radcomponents.Messenger");

/**
 * Example of a 'store' that exists outside of the perspective property trees.  Properties which are useful to users
 * should be held in the component props and defined by the component's property scheme (found in
 * perspective-component/common/src/main/resources/messager.props.json).  However, not all component state makes
 * sense as something you'd want to expose to your end user/designer.
 *
 * In this case we're creating a simple store that demonstrates some common patterns in mobx 'observables'.  One
 * observable gets updated when a 'message' is or should be pending.  As an 'observable' value, any 'observers' will
 * get called when the reference value changes.
 *
 * We define some 'observers' in the constructor through 'reactions', which are a construct to say "run a function when
 * an observable value I care about changes".  Defining the "observable value I care about' occurs in the first function
 * supplied to the mobx 'reaction()` (called the 'data' function in mobx terminology).  Any 'observable' property that
 * is de-referenced in that first function will result in a call to the second function when a value changes, providing
 * the return value as the parameter for the 'effect'.  See https://mobx.js.org/refguide/reaction.html for more info.
 *
 * Note that we never actually set the value of 'messageCount' except for in our 'reaction', which happens each
 * time the value of 'messagePending' switches from false to true.
 *
 */
export class CustomValueStore implements MessageResponseHandler {
    readonly delegate: MessageComponentGatewayDelegate;

    /**
     * Mobx reactions provide a 'disposer' upon creation.  These should be called when the store is no longer in use
     * to de-register it and avoid accumulation of listeners/subscribers to stores that don't exist.  Generally
     * this is called when a component is removed from the DOM by calling the disposer in React's
     * 'componentWillUnmount()' lifecycle is called.
     */
    messagePendingDisposer?: IReactionDisposer;

    /**
     * Observable value that is set when a message should be pending.  Setting this to 'true' will trigger a 'reaction'
     * that causes a message to be sent to the gateway.
     */
    @observable
    messagePending: boolean = false;

    /**
     * THe number of messages that have occurred between the gateway and the component store.
     */
    @observable
    messageCount: number = 0;

    constructor(delegate: MessageComponentGatewayDelegate){
        this.delegate = delegate;

        this.dispose = this.dispose.bind(this);

        delegate.setOnMessageReceivedCallback(this.handleMessageResponse);

        // this sets up a mobx reaction that says "any time there's a change to the reference held at
        // `this.messagesPending`, and that update made pending go from 'true' to 'false', add one to the count.
        this.messagePendingDisposer = reaction(() => this.messagePending, (pending) => {
            if (pending) {
                this.fireGwMessage();
            }
        });
    }

    public dispose() {
        this.messagePendingDisposer && this.messagePendingDisposer();
    }

    /**
     * Fires the message to the gateway. Annotated with the '@action' because it updates a mobx observable.
     * See more about actions at https://mobx.js.org/
     */
     private fireGwMessage() {
        this.delegate.messageGateway(this.messageCount);
    }


    /**
     * Callback that gets called from the store delegate when an app receives a message from the Gateway model delegate.
     * Annotated with a bound mobx action since it mutates observables and we want it to be explicit AND transactional.
     * @param payload the response from the gateway
     */
    @action.bound
    handleMessageResponse(payload: JsObject): void {

        logger.info(() => `Callback handling message with contents: ${JSON.stringify(payload)}`);

        if (payload && payload.count && !isNaN(payload.count)) {
            // here's the count our gateway is reporting.  We could use this directly...
            // @ts-ignore
            const gwCount: number = payload.count;

            // ...but for demonstration purposes, we'll just toggle our "pending" value.  Changing pending results in
            // reactions firing on any 'subscribed functions' or 'observers' that dereference the 'messagePending'
            // property (since it's observable).  In our case, the reaction we setup in this class' constructor will
            // respond and add one to our internal message count.  Since the count is observable and being observed
            // by our component, updating its value is all we need to do to have the component render the new value!
            this.messageCount += 1;
        } else if (payload && payload.error) {
            logger.error(() => `Error MessageDelegate Response: ${payload.error}`);
        } else {
            logger.error(() => `Detected no payload in response!`);
        }

        this.messagePending = false;
    }
}


/**
 * ComponentStoreDelegate provides a way to send (and receive) data from the gateway that does _not_ go through a
 * component's property trees or traditional http/ajax requests.  Instead messages are sent to/from the gateway
 * via perspective's websocket connection without any need to directly interact with the socket.
 *
 * This class is separate from the 'CustomValueStore' to avoid confusing two distinct concepts.  The use of a mobx
 * based store is in no way required in your ComponentStoreDelegate implementation.  This example just highlights
 * one way to have an 'un-managed' store (one which Perspective does not manage synchronizing values to/from the
 * gateway via PropertyTree).  The CustomStore could extend 'ComponentStoreDelegate' itself and avoid separate
 * classes for each if desired.
 *
 */
export class MessageComponentGatewayDelegate extends ComponentStoreDelegate {
    public static EVENT_NAME: string = "messenger-component-message-event";
    public static INCOMING_EVENT: string = "messenger-component-response-event";

    private callback: (payload: JsObject) => void;

    constructor(componentStore: AbstractUIElementStore){
        super(componentStore);
        this.messageGateway = this.messageGateway.bind(this);
        this.handleEvent = this.handleEvent.bind(this);
        this.setOnMessageReceivedCallback = this.setOnMessageReceivedCallback.bind(this);
        this.fireEvent = this.fireEvent.bind(this);
    }

    // not necessary to override, done so here for informational/educational purposes
    public fireEvent(eventName: string, eventObject: JsObject): void {
        // log messages left intentionally
        logger.info(() => `Firing ${eventName} event with message body ${eventObject}`);
        super.fireEvent(eventName, eventObject);
    }

    public messageGateway(count: Number): void {
        this.fireEvent(MessageComponentGatewayDelegate.EVENT_NAME, { count: count });
    }

    // receives the model events fired from the gateway side.
    handleEvent(eventName: string, eventObject: JsObject): void {
        logger.info(() => `Received '${eventName}' event!`);
        if (eventName === MessageComponentGatewayDelegate.INCOMING_EVENT) {

            if (this.callback && isFunction(this.callback)) {

                logger.info(() => `Calling store callback with delegate event payload ` +
                                  `${JSON.stringify(eventObject)}`
                );
                this.callback(eventObject);
            } else {
                logger.warn(() => `No callback detected for MessageComponentGatewayDelegate!`);
            }
        }
    }

    setOnMessageReceivedCallback(callback: (payload: JsObject) => void): void {
        this.callback = callback;
    }
}



/**
 * Main component implementation class.
 */
@observer   // observer because the internal store uses mobx
export class MessengerComponent extends Component<ComponentProps<MessagePropConfig>, {}> {

    // store used for storing state using mobx as an alternative to react `setState()`
    readonly myStore: CustomValueStore;
    readonly delegate: MessageComponentGatewayDelegate;


    constructor(props: ComponentProps<MessagePropConfig>) {
        super(props);
        this.delegate = props.delegate as MessageComponentGatewayDelegate;
        this.myStore = new CustomValueStore(this.delegate);
    }

    componentWillUnmount() {
        // call the disposer to get rid of the mobx tracking when this component is removed from the page
        this.myStore && this.myStore.dispose();
    }

    /**
     * Calling this sets the store's 'messagePending' value to true.  Doing so results in the firing of any subscribed
     * reactions. This is a mobx 'action' because it changes the value of a  mobx observable.
     */
    @action.bound
    fireUpdateToGateway(): void {
        logger.info(() => "Setting messagePending to true!");

        this.myStore.messagePending = true;
    }

    @bind
    renderMsg(): String {
        // get the correct message based on the number of messages that have been sent to/from the gateway.
        const msgItem = Object.entries(this.props.props.messageConfig)
            .filter(([k, v]: [string, string]) => /^[0-9]+$/.test(k))
            .map(([k, v]: [string, string]) => [Number(k), v])
            .sort((pair, pair2) => (pair[0] as number) - (pair2[0] as number))
            .reduce((accum, next) => {
                if (this.myStore.messageCount >= (next[0] as number)) {
                    return next;
                } else {
                    return accum;
                }
            }, [-1, "Default Message!"]);

        return msgItem[1] as string;
    }

    render() {
        const buttonText: string = this.myStore.messagePending ? "Waiting" : "Send Message";

        return (
            // note that the topmost piece of dom requires the use of the 'emitter' provided by props in order for
            // containers to appropriately position them.  The
            <div {...this.props.emit({classes: ["messenger-component"]})}>
                <h3 className={"counter"}>
                    {this.myStore.messageCount}
                </h3>
                <span className={"message"}>{this.renderMsg()}</span>
                <button className={"messenger-button"} onClick={this.fireUpdateToGateway} disabled={this.myStore.messagePending}>{buttonText}</button>
            </div>
        );
    }
}


// this is the actual thing that gets registered with the component registry
export class MessengerComponentMeta implements ComponentMeta {

    getComponentType(): string {
        return COMPONENT_TYPE;
    }

    getDefaultSize(): SizeObject {
        return ({
            width: 120,
            height: 90
        });
    }

    public createDelegate(component: AbstractUIElementStore): ComponentStoreDelegate | undefined {
        return new MessageComponentGatewayDelegate(component);
    }

    getViewComponent(): PComponent {
        return MessengerComponent;

    }

    getPropsReducer(tree: PropertyTree): MessagePropConfig {
        return { messageConfig: tree.read("messageConfig", DEFAULT_MESSAGE_CONFIG)};
    }
}
