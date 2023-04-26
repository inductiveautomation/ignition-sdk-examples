package io.ia.examples.resource;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class GatewayHook extends AbstractGatewayModuleHook {
    private GatewayContext context;

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
    }

    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean isMakerEditionCompatible() {
        return true;
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }
}
