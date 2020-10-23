package org.fakester.gateway.delegate;


import java.util.Arrays;
import java.util.stream.Collectors;

import com.inductiveautomation.ignition.common.gson.JsonElement;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.script.builtin.KeywordArgs;
import com.inductiveautomation.ignition.common.script.builtin.PyArgumentMap;
import com.inductiveautomation.perspective.gateway.api.Component;
import com.inductiveautomation.perspective.gateway.api.ComponentModelDelegate;
import com.inductiveautomation.perspective.gateway.api.ScriptCallable;
import com.inductiveautomation.perspective.gateway.messages.EventFiredMsg;
import org.python.core.Py;
import org.python.core.PyObject;

/**
 * Model Delegate for the Messenger component.
 */
public class MessageComponentModelDelegate extends ComponentModelDelegate {
    public static final String INCOMING_EVENT_NAME = "messenger-component-message-event";
    public static final String OUTBOUND_EVENT_NAME = "messenger-component-message-response-event";

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

    /**
     * Use the {@link ScriptCallable} annotation to mark a public method accessible to script authors interacting
     * with your component on the backend.
     *
     * Use of {@link PyArgumentMap} and the {@code (PyObject[], String[])} signature is optional, but encouraged, as
     * it allows script authors to use keyword arguments when using your methods.
     *
     * @param pyArgs The PyObjects supplied by the script author
     * @param keywords Any keywords supplied by the script author.
     */
    @ScriptCallable
    @KeywordArgs(names = {"objects", "sep", "end"}, types = {String.class, String.class, String.class})
    public void print(PyObject[] pyArgs, String[] keywords) {
        PyArgumentMap argumentMap =
            PyArgumentMap.interpretPyArgs(pyArgs, keywords, MessageComponentModelDelegate.class, "print");
        String[] objects = argumentMap.getStringArray("objects", new String[0]);

        if (objects == null) {
            throw Py.ValueError("print argument 'objects' cannot be None");
        }

        log.info(Arrays.stream(objects)
            .collect(Collectors.joining(
                argumentMap.getIfString("sep").orElse(" "),
                "",
                argumentMap.getIfString("end").orElse("\n")
            )));
    }

    // when a ComponentStoreDelegate event is fired from the client side, it comes through this method.
    @Override
    public void handleEvent(EventFiredMsg message) {
        log.infof("Received EventFiredMessage of type: %s", message.getEventName());

        // filter out the message we're interested in
        if (INCOMING_EVENT_NAME.equals(message.getEventName())) {
            JsonObject payload = message.getEvent();
            JsonObject responsePayload = new JsonObject();

            if (payload != null) {
                JsonElement count = payload.get("count");

                if (count.isJsonPrimitive() && count.getAsJsonPrimitive().isNumber()) {
                    int lastCount = count.getAsJsonPrimitive().getAsInt();
                    int next = lastCount + 1;
                    responsePayload.addProperty("count", next);
                } else {
                    responsePayload.addProperty("error", "Didn't detect count in Gateway Delegate!");
                }
            } else {
                responsePayload.addProperty("error",
                    "Gateway didn't receive a payload with '" + INCOMING_EVENT_NAME + "' event!");
            }
            fireEvent(OUTBOUND_EVENT_NAME, responsePayload);
        }
    }

    // not necessary to override for our use case, just here for informational purposes
    @Override
    public void fireEvent(String eventName, JsonObject event) {
        this.component.fireEvent("model", eventName, event);
    }
}
