package org.fakester.gateway.delegate;


import com.inductiveautomation.ignition.common.gson.Gson;
import com.inductiveautomation.ignition.common.gson.JsonElement;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.perspective.common.PerspectiveModule;
import com.inductiveautomation.perspective.gateway.api.Component;
import com.inductiveautomation.perspective.gateway.api.ComponentModelDelegate;
import com.inductiveautomation.perspective.gateway.messages.EventFiredMsg;

/**
 * Model Delegate for the Messenger component.
 */
public class MessageComponentModelDelegate extends ComponentModelDelegate {
    public static final String INCOMING_EVENT_NAME = "messenger-component-message-event";
    public static final String OUTBOUND_EVENT_NAME = "messenger-component-response-event";
    private static final Gson gson = PerspectiveModule.createPerspectiveCompatibleGson();

    public MessageComponentModelDelegate(Component component) {
        super(component);
    }

    @Override
    protected void onStartup() {
        // Called when the Gateway's ComponentModel starts.  The start itself happens when the client project is
        // loading and includes an instance of the the component type in the page/view being started.
        log.infof("Starting up delegate for '%s'!", component.getComponentAddressPath());
    }

    @Override
    protected void onShutdown() {
        // Called when the component is removed from the page/view and the model is shutting down.
        log.infof("Shutting down delegate for '%s'!", component.getComponentAddressPath());
    }

    // when a ComponentStoreDelegate event is fired from the client side, it comes through this method.
    // The message contains
    @Override
    public void handleEvent(EventFiredMsg message) {

        log.info("Received EventFiredMessage of type: " + message.getEventName());

        // filter out the message we're interested in
        if (INCOMING_EVENT_NAME.equals(message.getEventName())) {
            JsonObject payload = message.getEvent();
            JsonObject responsePayload = new JsonObject();

            if (payload != null) {
                JsonElement count = payload.get("count");

                if (payload.isJsonPrimitive() && payload.getAsJsonPrimitive().isNumber()) {
                    int lastCount = payload.getAsJsonPrimitive().getAsInt();
                    int next = lastCount + 1;
                    responsePayload.addProperty("count", next);
                } else {
                    responsePayload.addProperty("error", "Didn't detect count in Gateway Delegate!");
                }

                fireEvent(OUTBOUND_EVENT_NAME, responsePayload);
            } else {
                responsePayload.addProperty("error",
                    "Gateway didn't receive a payload with '" + INCOMING_EVENT_NAME + "' event!");
                fireEvent(OUTBOUND_EVENT_NAME, responsePayload);
            }
        }
    }

    @Override
    public void fireEvent(String eventName, JsonObject event) {
        this.component.fireEvent("model", eventName, event);
    }
}
