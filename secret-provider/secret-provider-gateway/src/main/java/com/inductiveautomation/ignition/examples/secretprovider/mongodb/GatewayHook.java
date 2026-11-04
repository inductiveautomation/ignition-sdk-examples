package com.inductiveautomation.ignition.examples.secretprovider.mongodb;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.config.ExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.NamedResourceHandler;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

import java.util.List;

/**
 * The GatewayHook is the main entry point for a Gateway module. It is instantiated very early in the Gateway startup
 * process, before most other services are available. It is responsible for registering extension points and other
 * module-level functionality.
 */
public class GatewayHook extends AbstractGatewayModuleHook {
    public static final String MODULE_ID = "com.inductiveautomation.ignition.examples.mongodb-secret-provider";

    private NamedResourceHandler<MongoDbSecretProviderResource> namedResourceHandler;

    static {
        // Register our localized properties with BundleUtil
        BundleUtil.get().addBundle(MongoDbSecretProvider.class);
    }

    @Override
    public void setup(GatewayContext context) {
        // Register our named resource handler for the MongoDbSecretProviderResource type.
        namedResourceHandler = NamedResourceHandler.newBuilder(MongoDbSecretProviderResource.META)
                .context(context)
                .build();
    }

    @Override
    public void startup(LicenseState licenseState) {
        namedResourceHandler.startup();
    }

    @Override
    public void shutdown() {
        namedResourceHandler.shutdown();
    }

    /**
     * In a change from the <=8.1 model, any and all extension points, no matter <b>which</b> extension point they
     * extend, must be declared in your GatewayHook. They will be separated by the gateway and the appropriate lifecycle
     * management will be handled for you.
     */
    @Override
    public List<? extends ExtensionPoint<?>> getExtensionPoints() {
        return List.of(
                new MongoDbSecretProviderExtensionPoint()
        );
    }
}
