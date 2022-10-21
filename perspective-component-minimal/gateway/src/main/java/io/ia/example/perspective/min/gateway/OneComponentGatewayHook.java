package io.ia.example.perspective.min.gateway;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import static io.ia.example.perspective.min.common.OneComponent.URL_ALIAS;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.perspective.common.api.ComponentRegistry;
import com.inductiveautomation.perspective.gateway.api.PerspectiveContext;
import io.ia.example.perspective.min.common.comp.Image;

public class OneComponentGatewayHook extends AbstractGatewayModuleHook {

    private static final LoggerEx log = LoggerEx.newBuilder().build("io.ia.example.gateway.OneComponentGatewayHook");

    private GatewayContext gatewayContext;
    private PerspectiveContext perspectiveContext;
    private ComponentRegistry componentRegistry;

    @Override
    public void setup(GatewayContext context) {
        this.gatewayContext = context;
        log.info("Setting up OneComponent module.");
    }

    @Override
    public void startup(LicenseState activationState) {
        log.info("Starting up OneComponentGatewayHook!");

        this.perspectiveContext = PerspectiveContext.get(this.gatewayContext);
        this.componentRegistry = this.perspectiveContext.getComponentRegistry();

        if (this.componentRegistry != null) {
            log.info("Registering OneComponent.");
            this.componentRegistry.registerComponent(Image.DESCRIPTOR);
        } else {
            log.error("Reference to component registry not found, OneComponent module will fail to function!");
        }

    }

    @Override
    public void shutdown() {
        log.info("Shutting down OneComponent module and removing registered components.");
        if (this.componentRegistry != null) {
            this.componentRegistry.removeComponent(Image.COMPONENT_ID);
        } else {
            log.warn("Component registry was null, could not unregister Rad Components.");
        }
    }

    @Override
    public Optional<String> getMountedResourceFolder() {
        return Optional.of("mounted");
    }

    // Lets us use the route http://<gateway>/res/OneComponents/*
    @Override
    public Optional<String> getMountPathAlias() {
        return Optional.of(URL_ALIAS);
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }

    @Override
    public void onMountedResourceRequest(String resourcePath, HttpServletResponse response) {
        super.onMountedResourceRequest(resourcePath, response);
    }
}
