/**
 * Example of a component that utilizes the ComponentStoreDelegate API to send and receive payload-containing messages
 * between the browser's instance of a component/store and the Gateway's model.
 *
 * This is the Client-Side implementation of a ComponentStoreDelegate. The API provides a convenient way to send
 * messages over the websocket established by the Perspective runtime.  A mirror API on the Gateway provides a
 * corresponding opportunity to send/receive messages from the Gateway.  Together, they provide a seamless 'realtime'
 * message channel for sending state to/from your client stores and server models.
 */

import * as React from 'react';
import {
    AbstractUIElementStore,
    Component,
    ComponentMeta,
    ComponentProps,
    ComponentStoreDelegate,
    JsObject,
    makeLogger,
    PComponent,
    PropertyTree,
    SizeObject
} from '@inductiveautomation/perspective-client';
import { bind } from 'bind-decorator';

// The 'key' or 'id' for this component type.  Component must be registered with this EXACT key in the Java side as well
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

// Default configuration in component props.  Added here just as a useful reference.  Generally only defined in
// the prop schema (props.json file).
export const DEFAULT_MESSAGE_CONFIG: MessagePropConfig = {
    "0": "None",
    "1": "Messages!",
    "5": "Lots of Messages!",
    "10": "Literally ten+ messages!",
    "25": "Carpal Tunnel Warning!"
};

const logger = makeLogger("radcomponents.Messenger");

interface MessengerDelegateState {
    messagePending: boolean;
    messageCount: number;
}

// These match events in the Gateway side component delegate.
enum MessageEvents {
    MESSAGE_REQUEST_EVENT = "messenger-component-message-event",
    MESSAGE_RESPONSE_EVENT = "messenger-component-message-response-event"
}

/**
 * ComponentStoreDelegate provides a way to communicate with a Gateway side implementation of ComponentModelDelegate.
 * Communication is done over Perspective's websocket connection. Incoming messages are mapped to implemented handlers 
 * in the abstract method `handleEvent`. The ComponentStoreDelegate can also be used to store local state if needed.
 * 
 * Tip: Avoid frequently sending large payloads over the websocket connection.  The websocket connection is
 * the primary way the front-end communicates with the backend (Gateway).  Sending large payloads often will cause traffic 
 * over the network potentially slowing other tasks relying on communication. Consider fetching the payload of a websocket 
 * message over HTTP instead to help alleviate some websocket traffic for large and frequent payloads.
 * 
 * Since the ComponentStoreDelegate is intended to be paired with a corresponding Perspective component implementation, 
 * its state, if any, is meant to be passed to the paired component via the component's `props`.  This is done by invoking 
 * `mapStateToProps` of the ComponentStoreDelegate by the paired component's Higher Order Component (HOC) 
 *  once the delegate notifies listeners of state change.
 * 
 * The MessageComponentGateway delegate inherits these methods from the ComponentStoreDelegate abstract class:
 * 
 * @method `public subscribe(listener: Function): Function` - public methods used by listeners to subscribe to state changes or notifications.  
 * Returns a disposer function that should be use to unsubscribe and remove reference to the listener.  Use this to prevent memory leaks where necessary.
 * @method `protected notify(): void` - called by the delegate as a means to notify any subscribed listeners of state changes. Listeners will then read the new values.
 * @method `abstract handleEvent(eventName: string, eventObject: PlainObject): void` - receives model events from the Gateway side delegate over the websocket connection.
 * @method `fireEvent(eventName: string, eventObject: PlainObject): void` - fires model event that is received by the Gateway side delegate over the websocket connection.
 * @method `mapStateToProps(): PlainObject` - invoked by the HOC wrapper component to map any delegate state to component props when the HOC is notified of state changes.
 * 
 */
export class MessageComponentGatewayDelegate extends ComponentStoreDelegate {
    /**
     * Value that is set when a message should be pending.
     */
    private messagePending: boolean = false;

    /**
     * The number of messages that have occurred between the Gateway and this delegate.
     */
    private messageCount: number = 0;
    
    constructor(componentStore: AbstractUIElementStore) {
        // Required initialization of super.
        super(componentStore);
    }
    
    /**
     * Maps our delegate state to component props. Invoked by the components HOC
     * Wrapper component in the ComponentStore and passed to the Perspective 
     * component in props, i.e. `this.props.props.delegate`.  Will do so
     * whenever this delegate notifies listeners of state changes.
     */
    mapStateToProps(): MessengerDelegateState {
        return {
            messageCount: this.messageCount,
            messagePending: this.messagePending
        };
    }

    public fireDelegateEvent(eventName: string, eventObject: JsObject): void {
        // log messages left intentionally
        logger.info(() => `Firing ${eventName} event with message body ${eventObject}`);
        // Inherited from the ComponentStoreDelegate abstract class.
        this.fireEvent(eventName, eventObject);
        
        // Set messagePending to true since an event has been sent to the Gateway.
        // Will be set to false when a response event is received from the Gateway.
        this.messagePending = true;
        // Notify listeners of changes to message pending. This will update the props 
        // (this.props.delegate) being passed to the paired component (MessengerComponent).
        this.notify();
    }

    // Used by our component to fire a message to the gateway.
    public fireGatewayMessage(): void {
        this.fireDelegateEvent(MessageEvents.MESSAGE_RESPONSE_EVENT, { count: this.messageCount });
    }

    /**
     * Implements `handleEvent` of abstract class ComponentStoreDelegate.
     * 
     * Will automatically be invoked whenever a message is sent down from the corresponding delegate 
     * on the backend. Here we map messages and their payloads to the appropriate handlers.
     */
    handleEvent(eventName: string, eventObject: JsObject): void {
        logger.info(() => `Received '${eventName}' event!`);
        const {
            MESSAGE_RESPONSE_EVENT
        } = MessageEvents;

        switch (eventName) {
            case MESSAGE_RESPONSE_EVENT:
                this.handleComponentResponseEvent(eventObject);
                break;
            default:
                logger.warn(() => `No delegate event handler found for event: ${eventName} in MessageComponentGatewayDelegate`);
        }
    }

    handleComponentResponseEvent(eventObject: JsObject): void {
        logger.info(() => `Callback handling message with contents: ${JSON.stringify(eventObject)}`);

        if (eventObject && !isNaN(eventObject.count)) {
            if (
                this.messageCount !== eventObject.count ||
                this.messagePending !== false
            ) {
                // The count sent to the Gateway side component delegate where it is 
                // incremented by one and then returned in this response event.
                this.messageCount = eventObject.count;
                // A response event has been received.  Message is no longer pending. Reset state to false.
                this.messagePending = false;
                // Notify listeners of changes to internal state. This will update the props 
                // (this.props.delegate) being passed to the paired component (MessengerComponent).
                this.notify();
            }

        } else if (eventObject && eventObject.error) {
            logger.error(() => `Error MessageDelegate Response: ${eventObject.error}`);
        } else {
            logger.error(() => `Detected no payload in response!`);
        }
    }
}

/**
 * Our Perspective component, written as a React class component.  Functional components are
 * also acceptable if you choose to make use of React's Hook API, or simply prefer writing functional components. 
 * 
 * MessengerDelegateState is optional and only useful when a component actually has
 * a registered ComponentDelegate as it does in this example.
 */
export class MessengerComponent extends Component<ComponentProps<MessagePropConfig, MessengerDelegateState>, {}> {
    @bind
    fireUpdateToGateway(): void {
        // We know a delegate exists because we've set implemented `createDelegate` in 
        // our components meta definition. Access the delegate directly via this component's 
        // ComponentStore in order to invoke methods of our delegate.  Passing methods via 
        // props (this.props.delegate.someMethodOrCallback) can also work, and is a common React pattern.
        logger.info(() => "Firing message to the Gateway!");
        (this.props.store.delegate! as MessageComponentGatewayDelegate).fireGatewayMessage();
    }

    @bind
    renderMsg(): String {
        const messageCount = this.props.delegate!.messageCount;
        // Get the correct message based on the number of messages that have been sent to/from the Gateway.
        const msgItem = Object.entries(this.props.props.messageConfig)
            .filter(([k, v]: [string, string]) => /^[0-9]+$/.test(k))
            .map(([k, v]: [string, string]) => [Number(k), v])
            .sort((pair, pair2) => (pair[0] as number) - (pair2[0] as number))
            .reduce((accum, next) => {
                if (messageCount >= (next[0] as number)) {
                    return next;
                } else {
                    return accum;
                }
            }, [-1, "Default Message!"]);

        return msgItem[1] as string;
    }

    render() {
        const buttonText: string = this.props.delegate!.messagePending ? "Waiting" : "Send Message";
        return (
            // Note that the topmost piece of DOM requires the use of the 'emitter' provided by props in order for
            // containers to appropriately position them in addition to attaching event listeners and styles.  
            // Adding your own events here or styles here outside of the emitter will cause an override depending on 
            // the order in which they are declared relative to the invocation of the emitter. Add inline styles and classes
            // as shown below.  Add event listeners by using `this.props.domEvents.addListener` in the constructor or on mount,
            // and be sure to remove these listeners to prevent memory leaks on un-mount.  
            <div {...this.props.emit({ classes: ["messenger-component"] })}>
                <h3 className="counter">
                    {this.props.delegate!.messageCount}
                </h3>
                <span className="message">{this.renderMsg()}</span>
                <button
                    className="messenger-button"
                    onClick={this.fireUpdateToGateway}
                    disabled={this.props.delegate!.messagePending}
                >
                    {buttonText}
                </button>
            </div>
        );
    }
}


// This is the actual thing that gets registered with the component registry.
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

    createDelegate(component: AbstractUIElementStore): ComponentStoreDelegate | undefined {
        return new MessageComponentGatewayDelegate(component);
    }

    getViewComponent(): PComponent {
        return MessengerComponent;

    }

    getPropsReducer(tree: PropertyTree): MessagePropConfig {
        return { messageConfig: tree.read("messageConfig", DEFAULT_MESSAGE_CONFIG) };
    }
}
