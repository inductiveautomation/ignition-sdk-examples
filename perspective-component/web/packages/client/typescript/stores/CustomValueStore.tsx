import * as React from 'react';
import {
    createSubscriptionMap,
    NotificationHandler,
    subscriptionFactory,
    SubscriptionHandler,
    SubscriptionMap
} from '@inductiveautomation/perspective-client';

// You determine the available state.
export enum CustomValueStoreState {
    customValue = 'customValue'
}

/**
 * An example of a store that exists outside of the Perspective property trees.  Not all state makes
 * sense as something you'd want to expose to your end user/designer. This is a simple store that demonstrates 
 * use of our lightweight subscription pattern.  You are not limited or bound to using this pattern. There are 
 * plenty of other ways to manage state (Redux, MobX, Immer, etc.) and make interested parties aware of changes.
 */
export class CustomValueStore {
    // Our subscription map.  Under the hood, a plain object of keys representing
    // state, and values which are an array of subscribed listeners.
    // By default, the subscription map will also allow the subscription of
    // "any state change" listeners, meaning they do not need to specify a particular
    // state value they are interested in when subscribing to our store. 
    // You can opt out of this if you'd like. For example:
    // store.subscribe(myAnyChangeListener) vs. 
    // store.subscribe(myCustomValueListener, CustomValueStoreState.customValue))
    private subscriptionMap: Readonly<SubscriptionMap> =
        createSubscriptionMap(Object.values(CustomValueStoreState));
    // Used by outsiders to subscribe to state changes.
    readonly subscribe: SubscriptionHandler;
    private notify: NotificationHandler;

    private _customValue: string = 'foo';

    constructor() {
        const {
            // Returns everything we need for our subscription pattern.
            subscribe,
            notify
        } = subscriptionFactory(
            this.subscriptionMap, // Our subscription map
            'CustomValueStore' // For logging purposes
        );
        this.subscribe = subscribe;
        this.notify = notify;
    }

    get customValue(): string {
        return this._customValue;
    }

    public updateCustomValue(newValue: string): void {
        if (this._customValue !== newValue) {
            this._customValue = newValue;
            // Notify listeners of changes. Will also notify 
            // 'any state change' listeners automatically if configured.
            this.notify(CustomValueStoreState.customValue);
        }
    }

}

// Example of a store that is interested in the state of our CustomValueStore.
export class AnotherStore {
    customValueStore: CustomValueStore;
    constructor(store: CustomValueStore) {
        this.customValueStore = store;
        // Assuming our store is a singleton, there may be no need to 'unsubscribe' listeners.
        // So, we do not store the returned disposer function of `subscribe` to later dispose of the reference.
        this.customValueStore.subscribe(this.onCustomValueChange, CustomValueStoreState.customValue);
        this.customValueStore.subscribe(this.onCustomValueStoreAnyStateChange);
    }

    // Invoked whenever CustomValueStoreState.customValue changes. But,
    // truly whenever the CustomValueStore notifies using the `customValue` key.
    onCustomValueChange(): void {
        const newCustomValue = this.customValueStore.customValue;
        console.log(`Custom value changed: ${newCustomValue}`);
    }

    // Invoked whenever any state changes.
    onCustomValueStoreAnyStateChange(): void {
        console.log(`CustomValueStore any state changed: ${this.customValueStore.customValue}`);
    }
}

interface SomeComponentProps {
    store: CustomValueStore;
}

// Example of a React component that is interested in the state of the CustomValueStore.
export class SomeComponent extends React.Component<SomeComponentProps> {
    customValueListenerDisposer?: Function;

    componentDidMount() {
        // Subscribe to our CustomValueStore and store the disposer function so that we can remove
        // the reference and prevent memory leaks when this component unmounts.
        this.customValueListenerDisposer =
            this.props.store.subscribe(this.onCustomValueChanged, CustomValueStoreState.customValue);
    }

    componentWillUnmount() {
        // Remove reference/unsubscribe/dispose of the listener to prevent memory leaks.
        this.customValueListenerDisposer && this.customValueListenerDisposer();
    }

    onCustomValueChanged = () => {
        const newCustomValue = this.props.store.customValue;
        console.log(`Custom value changed: ${newCustomValue}`);
    }

    render() {
        return <div />;
    }
}
