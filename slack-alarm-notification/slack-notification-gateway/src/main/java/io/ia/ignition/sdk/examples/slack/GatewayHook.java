package io.ia.ignition.sdk.examples.slack;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileConfig;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.config.ExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.NamedResourceHandler;
import com.inductiveautomation.ignition.gateway.config.SingletonResourceHandler;
import com.inductiveautomation.ignition.gateway.config.migration.ExtensionPointRecordMigrationStrategy;
import com.inductiveautomation.ignition.gateway.config.migration.IdbMigrationStrategy;
import com.inductiveautomation.ignition.gateway.config.migration.NamedRecordMigrationStrategy;
import com.inductiveautomation.ignition.gateway.config.migration.SingletonRecordMigrationStrategy;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import io.ia.ignition.sdk.examples.slack.profile.*;

import java.util.List;

import static io.ia.ignition.sdk.examples.slack.profile.SlackNotificationExtensionPoint.SLACK_WEBHOOK;

public class GatewayHook extends AbstractGatewayModuleHook {
    public static final String MODULE_ID = "io.ia.ignition.sdk.examples.slack-notification";

    private GatewayContext gatewayContext;

    private SingletonResourceHandler<DemoSingletonResource> singletonResourceHandler;
    private NamedResourceHandler<DemoNamedResource> namedResourceHandler;

    @Override
    public void setup(GatewayContext context) {
        this.gatewayContext = context;

        // Register our localized properties with BundleUtil
        BundleUtil.get().addBundle("SlackNotification", getClass(), "SlackNotification");

        // This allows local overrides in the alarming section of the tag edit config
        context.getAlarmManager()
                .registerExtendedConfigProperties(MODULE_ID, SlackProperties.CUSTOM_MESSAGE);

        context.getUserSourceManager().registerContactType(SLACK_WEBHOOK);

        /*
         ResourceHandlers are used to abstract over the error-prone lifecycle management around configuration.
         As changes are made to resources, via the web UI, the REST API, or any other source, those changes will be
         collected and dispatched as ChangeOperations, which will be monitored by appropriately constructed resource
         handlers.
         You should restrict your interaction with the configuration manager to these built in helpers unless you really
         know what you're doing.
        */

        /*
         A "singleton" resource has no name - they simply exist, and should be seeded with some default config
         in your resource type meta. Then, you can watch it as changes are made. This is a great use case for "global"
         settings that should be set once per gateway.
        */
        singletonResourceHandler = SingletonResourceHandler.newBuilder(DemoSingletonResource.META)
                .context(context)
//                 .onChange() // add a listener that will be invoked whenever this resource changes
                .build();

        /*
         Named resources, obviously, have a name, which allows them to be disambiguated. In an important distinction
         from the standard project system, configuration does *not* support folders/hierarchy - names are effectively
         all in the same root folder and therefore must be unique.
         note that by default, renaming a resource is modelled as deleting the old one and creating a new one
         if you need to maintain state or otherwise track renames more intelligently, you must subclass
         NamedResourceHandler and override `isRenameAware` to return true.
        */
        namedResourceHandler = NamedResourceHandler.newBuilder(DemoNamedResource.META)
                .context(context)
//                .filter() // a predicate to determine whether a given config resource should be considered "live"
//                .onInitialResources() // called on startup with the existing list of configs
//                .onResourceAdded()
//                .onResourceRemoved()
//                .onResourceUpdated() // basic CRUD listeners
//                .onResourcesUpdated() // handler for a collection of resource changes
                .build();
    }

    @Override
    public void shutdown() {
        BundleUtil.get().removeBundle("SlackNotification");

        gatewayContext.getUserSourceManager().unregisterContactType(SLACK_WEBHOOK);

        singletonResourceHandler.shutdown();
        namedResourceHandler.shutdown();
    }

    @Override
    public void startup(LicenseState licenseState) {
        /*
        It's required to explicitly start up your managers to ensure they're watching for changes and deliver initial
        events.
        */
        singletonResourceHandler.startup();
        namedResourceHandler.startup();

        /*
         Once started, you can interact with the instances in various ways if you need to programmatically _push_
         configuration into the system:
         (all these methods return futures you can use to check completion)
        */
//        singletonResourceHandler.updateResource(newConfig, "someActor");
//        namedResourceHandler.create("name", newConfig, "someActor");
//        namedResourceHandler.modify("name", newConfig);
//        namedResourceHandler.delete("name");
        /*
        You can also retrieve current configuration at any point, as needed in your own code:
        */
//        DecodedResource<DemoNamedResource> decodedResource = namedResourceHandler.findResource("name").orElseThrow();
//        DemoSingletonResource resource = singletonResourceHandler.getResource();
        /*
        Or, just retrieve all configuration:
        */
//        List<DecodedResource<DemoNamedResource>> resources = namedResourceHandler.getResources();
    }

    /**
     * Here we tell the configuration management system about our "migration strategy", which adapts the legacy <=8.1
     * "PersistentRecord" storage to our new configuration management approach. You can use one of the existing builders
     * here, such as {@link ExtensionPointRecordMigrationStrategy}, {@link NamedRecordMigrationStrategy},
     * or {@link SingletonRecordMigrationStrategy}, or implement your own entirely via the {@link IdbMigrationStrategy}
     * interface.
     */
    @SuppressWarnings("deprecation")
    @Override
    public List<IdbMigrationStrategy> getRecordMigrationStrategies() {
        return List.of(ExtensionPointRecordMigrationStrategy
                .newBuilder(SlackNotificationExtensionPoint.TYPE_ID)
                .resourceType(AlarmNotificationProfileConfig.RESOURCE_TYPE)
                .profileMeta(AlarmNotificationProfileRecord.META)
                .settingsRecordForeignKey(SlackNotificationProfileSettings.Profile)
                .settingsMeta(SlackNotificationProfileSettings.META)
                .build()
        );
    }

    /**
     * In a change from the <=8.1 model, any and all extension points, no matter <b>which</b> extension point they
     * extend, must be declared in your GatewayHook. They will be separated by the gateway and the appropriate lifecycle
     * management will be handled for you.
     */
    @Override
    public List<? extends ExtensionPoint<?>> getExtensionPoints() {
        return List.of(new SlackNotificationExtensionPoint());
    }
}
