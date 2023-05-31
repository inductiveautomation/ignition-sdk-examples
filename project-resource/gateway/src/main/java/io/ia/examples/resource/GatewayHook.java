package io.ia.examples.resource;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectLifecycleFactory;
import org.apache.poi.ss.formula.functions.Even;

public class GatewayHook extends AbstractGatewayModuleHook {
    private GatewayContext context;
    private EventHandlerLifecycleFactory factory;

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
        this.factory = new EventHandlerLifecycleFactory(context);
    }

    @Override
    public void startup(LicenseState licenseState) {
        factory.startup();
    }

    @Override
    public void shutdown() {
        factory.shutdown();
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
