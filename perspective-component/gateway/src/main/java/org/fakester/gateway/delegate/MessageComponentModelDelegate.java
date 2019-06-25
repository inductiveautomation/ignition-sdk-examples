package org.fakester.gateway.delegate;


import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.perspective.gateway.api.Component;
import com.inductiveautomation.perspective.gateway.api.ComponentModelDelegate;
import com.inductiveautomation.perspective.gateway.messages.EventFiredMsg;

/**
 * Model Delegate for the Messenger component.
 */
public class MessageComponentModelDelegate extends ComponentModelDelegate {
    public MessageComponentModelDelegate(Component component) {
        super(component);
    }

    @Override
    protected void onStartup() {
        // Called when the Gateway's ComponentModel starts.  The start itself happens when the client project is
        // loading and includes an instance of the the component type in the page/view being started.
    }

    @Override
    protected void onShutdown() {
        // Called when the component is removed from the page/view and the model is shutting down.
    }

    // when a ComponentStoreDelegate event is fired from the client side, it comes through this method.
    // The message contains
    @Override
    public void handleEvent(EventFiredMsg message) {

        super.handleEvent(message);

    }

    @Override
    public void fireEvent(String eventName, JsonObject event) {
        this.component.fireEvent("model", eventName, event);
    }
}
