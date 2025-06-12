package com.inductiveautomation.ignition.examples.eventstream.source.gateway;

import com.inductiveautomation.eventstream.gateway.EventStreamManager;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/**
 * This class holds code that is specific to the event stream module. This is to prevent the GatewayHook from crashing
 * on a {@link ClassNotFoundException} when the event stream module is not installed. This class is only loaded if the
 * event stream module is installed via the following check:
 * (by #{@link ExampleSourceGatewayHook#eventStreamLoaded(GatewayContext)}). Linking will then not throw a
 * {@link ClassNotFoundException} fail, since the {@link EventStreamManager} is guaranteed to be available.
 */
public class EventStreamInstaller {

    public static void setup(GatewayContext context) {
        EventStreamManager.get(context).getSourceRegistry().register(
            ExampleSource.createFactory()
        );
    }

}
