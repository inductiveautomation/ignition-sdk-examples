package com.inductiveautomation.ignition.examples.eventstream.source.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceModule;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.ModuleState;

public class ExampleSourceGatewayHook extends AbstractGatewayModuleHook {
    private final LoggerEx logger = LoggerEx.newBuilder().build(ExampleSourceGatewayHook.class);

    @Override
    public void setup(GatewayContext context) {
        if (eventStreamLoaded(context)) {
            EventStreamInstaller.setup(context);
        } else {
            var warningMessage = "The Event Stream module is not installed or not active. "
                                 + "The ExampleSource will not be available. "
                                 + "Please install or enable the Event Stream module to use this module.";
            logger.warn(warningMessage);
            throw new IllegalStateException(warningMessage);
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
