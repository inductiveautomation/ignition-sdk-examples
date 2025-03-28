package com.inductiveautomation.ignition.examples.eventstream.source.gateway;

import com.inductiveautomation.eventstream.gateway.EventStreamManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceModule;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class ExampleSourceGatewayHook extends AbstractGatewayModuleHook {

    @Override
    public void setup(GatewayContext context) {

        // checks if the event stream module is installed
        if (context.getModule(ExampleSourceModule.EVENT_STREAM_MODULE_ID) != null) {
            EventStreamManager.get(context).getSourceRegistry().register(
                ExampleSourceModule.MODULE_ID,
                ExampleSource.createFactory()
            );
        }
    }

    @Override
    public void startup(LicenseState activationState) {

    }

    @Override
    public void shutdown() {

    }
}
