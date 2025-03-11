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
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import io.ia.ignition.sdk.examples.slack.profile.*;

import java.util.List;

import static io.ia.ignition.sdk.examples.slack.profile.SlackNotificationExtensionPoint.SLACK_WEBHOOK;

public class GatewayHook extends AbstractGatewayModuleHook {
    public static final String MODULE_ID = "io.ia.ignition.sdk.examples.slack-notification";

    private GatewayContext gatewayContext;

    // SingletonResourceHandler/NamedResourceHandler are akin to the `ProjectLifecycleFactory` concept
    // from the project system, but obviously apply to configuration
    // n.b. 'named' config resources are always at a flat level of organization
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

        singletonResourceHandler = SingletonResourceHandler.newBuilder(DemoSingletonResource.META)
                .context(context)
                .build();
        namedResourceHandler = NamedResourceHandler.newBuilder(DemoNamedResource.META)
                .context(context)
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
        singletonResourceHandler.startup();
        namedResourceHandler.startup();
    }

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

    @Override
    public List<? extends ExtensionPoint<?>> getExtensionPoints() {
        return List.of(new SlackNotificationExtensionPoint());
    }
}
