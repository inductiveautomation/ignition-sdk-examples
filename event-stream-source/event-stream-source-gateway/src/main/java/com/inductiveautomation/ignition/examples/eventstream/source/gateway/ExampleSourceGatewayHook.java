package com.inductiveautomation.ignition.examples.eventstream.source.gateway;

import com.inductiveautomation.eventstream.gateway.EventStreamManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class ExampleSourceGatewayHook extends AbstractGatewayModuleHook {

    @Override
    public void setup(GatewayContext context) {
        EventStreamManager.get(context).getSourceRegistry().register(
            com.inductiveautomation.examples.eventstream.source.ExampleSourceModule.MODULE_ID,
            ExampleSource.createFactory()
        );
    }

    @Override
    public void startup(LicenseState activationState) {

    }

    @Override
    public void shutdown() {

    }
}
