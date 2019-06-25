package org.fakester.gateway;

import java.util.Optional;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.GatewayModule;
import com.inductiveautomation.perspective.common.PerspectiveModule;
import com.inductiveautomation.perspective.common.api.ComponentRegistry;
import com.inductiveautomation.perspective.gateway.GatewayHook;
import com.inductiveautomation.perspective.gateway.api.ComponentModelDelegateRegistry;
import com.inductiveautomation.perspective.gateway.api.PerspectiveContext;
import org.fakester.common.component.display.Messenger;
import org.fakester.gateway.delegate.MessageComponentModelDelegate;
import org.fakester.gateway.endpoint.DataEndpoints;
import org.fakester.common.RadComponents;
import org.fakester.common.component.display.Image;
import org.fakester.common.component.display.TagCounter;

public class RadGatewayHook extends AbstractGatewayModuleHook {

    private static final LoggerEx log = LoggerEx.newBuilder().build("rad.gateway.hook");

    private GatewayContext gatewayContext;
    private PerspectiveContext perspectiveContext;
    private ComponentRegistry componentRegistry;
    private ComponentModelDelegateRegistry modelDelegateRegistry;
    private DataEndpoints routes;

    @Override
    public void setup(GatewayContext context) {
        this.gatewayContext = context;
    }

    @Override
    public void startup(LicenseState activationState) {
        this.perspectiveContext = PerspectiveContext.get(this.gatewayContext);
        this.componentRegistry = this.perspectiveContext.getComponentRegistry();
        this.modelDelegateRegistry = this.perspectiveContext.getComponentModelDelegateRegistry();

        if (this.componentRegistry != null) {
            this.componentRegistry.registerComponent(Image.DESCRIPTOR);
            this.componentRegistry.registerComponent(TagCounter.DESCRIPTOR);
            this.componentRegistry.registerComponent(Messenger.DESCRIPTOR);
        } else {
            log.error("Reference to component registry not found, Rad Components will fail to function!");
        }

        if (this.modelDelegateRegistry != null) {
            this.modelDelegateRegistry.register(Messenger.COMPONENT_ID, MessageComponentModelDelegate::new);
        }

    }

    @Override
    public void shutdown() {
        //
        this.componentRegistry.removeComponent(Image.COMPONENT_ID);
        this.componentRegistry.removeComponent(TagCounter.COMPONENT_ID);
        this.componentRegistry.removeComponent(Messenger.COMPONENT_ID);

        if (this.modelDelegateRegistry != null ) {
            this.modelDelegateRegistry.remove(Messenger.COMPONENT_ID);
        }

    }

    @Override
    public Optional<String> getMountedResourceFolder() {
        return Optional.of("mounted");
    }

    @Override
    public void mountRouteHandlers(RouteGroup routeGroup) {
        // where you may choose to implement web server endpoints accessible via `host:port/system/data/
        routes = new DataEndpoints(this.perspectiveContext, routeGroup);
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
