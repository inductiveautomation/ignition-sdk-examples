package com.inductiveautomation.ignition.examples.secretprovider.mongodb;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.config.ExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.NamedResourceHandler;
import com.inductiveautomation.ignition.gateway.config.migration.ExtensionPointRecordMigrationStrategy;
import com.inductiveautomation.ignition.gateway.config.migration.IdbMigrationStrategy;
import com.inductiveautomation.ignition.gateway.config.migration.NamedRecordMigrationStrategy;
import com.inductiveautomation.ignition.gateway.config.migration.SingletonRecordMigrationStrategy;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

import java.util.Collections;
import java.util.List;

/**
 * The GatewayHook is the main entry point for a Gateway module. It is instantiated very early in the Gateway startup
 * process, before most other services are available. It is responsible for registering extension points, migration
 * strategies, and other module-level functionality.
 */
public class GatewayHook extends AbstractGatewayModuleHook {
    public static final String MODULE_ID = "com.inductiveautomation.ignition.examples.mongodb-secret-provider";

    private NamedResourceHandler<MongoDbSecretProviderResource> namedResourceHandler;

    @Override
    public void setup(GatewayContext context) {

        // Register our localized properties with BundleUtil
        BundleUtil.get().addBundle("MongoDbSecretProvider", getClass(), "MongoDbSecretProvider");

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
        BundleUtil.get().removeBundle("MongoDbSecretProvider");
        namedResourceHandler.shutdown();
    }

    /**
     * Here we tell the configuration management system about our "migration strategy", which adapts the legacy <=8.1
     * "PersistentRecord" storage to our new configuration management approach. You can use one of the existing builders
     * here, such as {@link ExtensionPointRecordMigrationStrategy}, {@link NamedRecordMigrationStrategy},
     * or {@link SingletonRecordMigrationStrategy}, or implement your own entirely via the {@link IdbMigrationStrategy}
     * interface.
     */
    @Override
    public List<IdbMigrationStrategy> getRecordMigrationStrategies() {
        // This sample wasn't available for <=8.1, so we don't need any migration strategies as there are no records
        // to migrate.
        //
        // In fact, we could just omit this method entirely, as the default implementation returns an empty list.
        // However, we include it here for demonstration purposes.
        return Collections.emptyList();
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
