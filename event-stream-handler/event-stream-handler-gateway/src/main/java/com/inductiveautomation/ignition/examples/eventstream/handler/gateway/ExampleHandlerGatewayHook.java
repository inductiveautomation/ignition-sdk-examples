package com.inductiveautomation.ignition.examples.eventstream.handler.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.eventstream.handler.ExampleHandlerModule;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.GatewayModule;
import com.inductiveautomation.ignition.gateway.model.ModuleState;

public class ExampleHandlerGatewayHook extends AbstractGatewayModuleHook {
    private final LoggerEx logger = LoggerEx.newBuilder().build(ExampleHandlerGatewayHook.class);

    @Override
    public void setup(GatewayContext context) {
        if (eventStreamLoaded(context)) {
            EventStreamInstaller.setup(context);
        } else {
            String warningMessage = "The Event Stream module is not installed or not active. "
                    + "The ExampleHandler will not be available. "
                    + "Please install or enable the Event Stream module to use this module.";
            logger.warn(warningMessage);
            throw new IllegalStateException(warningMessage);
        }
    }

    private boolean eventStreamLoaded(GatewayContext context) {
        GatewayModule eventStreamModule = context.getModuleManager()
            .getModule(ExampleHandlerModule.EVENT_STREAM_MODULE_ID);

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
