package com.inductiveautomation.ignition.examples.eventstream.handler.gateway;

import com.inductiveautomation.eventstream.gateway.EventStreamManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.examples.eventstream.handler.ExampleHandlerModule;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class ExampleHandlerGatewayHook extends AbstractGatewayModuleHook {

    @Override
    public void setup(GatewayContext context) {
        EventStreamManager.get(context).getHandlerRegistry().register(
            ExampleHandlerModule.MODULE_ID,
            ExampleHandler.createFactory()
        );
    }

    @Override
    public void startup(LicenseState activationState) {

    }

    @Override
    public void shutdown() {

    }
}
