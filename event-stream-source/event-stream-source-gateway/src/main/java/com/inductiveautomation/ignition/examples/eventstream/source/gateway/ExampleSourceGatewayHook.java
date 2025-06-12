package com.inductiveautomation.ignition.examples.eventstream.source.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceModule;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.ModuleState;

public class ExampleSourceGatewayHook extends AbstractGatewayModuleHook {

    @Override
    public void setup(GatewayContext context) {
        if (eventStreamLoaded(context)) {
            EventStreamInstaller.setup(context);
        }
    }

    private boolean eventStreamLoaded(GatewayContext context) {
        var eventStreamModule = context.getModuleManager()
            .getModule(ExampleSourceModule.EVENT_STREAM_MODULE_ID);

        return eventStreamModule != null
               && (eventStreamModule.getState() == ModuleState.PENDING
                   || eventStreamModule.getState() == ModuleState.ACTIVE);
    }

    @Override
    public void startup(LicenseState activationState) {

    }

    @Override
    public void shutdown() {

    }
}
