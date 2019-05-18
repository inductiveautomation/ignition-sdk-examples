package org.fakester.gateway;

import java.util.Optional;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.perspective.gateway.api.PerspectiveContext;
import org.fakester.common.RadComponents;
import org.fakester.common.component.display.Image;

public class RadGatewayHook extends AbstractGatewayModuleHook {

    private GatewayContext context;

    @Override
    public void setup(GatewayContext context) {
        this.context = context;
    }

    @Override
    public void startup(LicenseState activationState) {
        PerspectiveContext.get(context)
            .getComponentRegistry()
            .registerComponent(Image.DESCRIPTOR);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public Optional<String> getMountedResourceFolder() {
        return Optional.of("mounted");
    }

    @Override
    public void mountRouteHandlers(RouteGroup routes) {

    }

    // Lets us use the route http://<gateway>/res/radcomponents/*
    @Override
    public Optional<String> getMountPathAlias() {
        return Optional.of(RadComponents.URL_ALIAS);
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }
}
