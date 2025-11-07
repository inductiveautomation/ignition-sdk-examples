package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfile;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileConfig;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileExtensionPoint;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.gateway.audit.AuditProfileType;
import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.SchemaUtil;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.nav.ExtensionPointResourceForm;
import com.inductiveautomation.ignition.gateway.web.nav.WebUiComponent;

import java.util.Optional;


/**
 * This is an example of implementing an existing "extension point" from Ignition; specifically, an alarm notification
 * profile.
 * <p>
 * Extension points are the most common way for modules to interface smoothly with Ignition, and knowing which extension
 * point you are extending is a core part of working with Ignition.
 */
public class SlackNotificationExtensionPoint
        extends AlarmNotificationProfileExtensionPoint<SlackNotificationProfileResource> {
    public static final String TYPE_ID = "SlackType";
    public static final ContactType SLACK_WEBHOOK =
            new ContactType("Slack", new LocalizedString("SlackNotification.ContactType.Slack"));

    public SlackNotificationExtensionPoint() {
        super(TYPE_ID,
                "SlackNotification.SlackNotificationProfileType.DisplayName",
                "SlackNotification.SlackNotificationProfileType.Description",
                SlackNotificationProfileResource.class);

        /*
         Add a "reference property", so that the gateway knows we're using an audit profile's name in our config
         If something tries to delete that audit profile, it will be prevented
         If that audit profile is renamed, it will update our config (per the lambda below)
        */
        addReferenceProperty(
                "auditProfileName",
                builder -> builder
                        .value(SlackNotificationProfileResource::auditProfileName)
                        .targetType(AuditProfileType.RESOURCE_TYPE)
                        .onUpdate((oldResource, newName) ->
                                new SlackNotificationProfileResource(newName)
                        )
        );
    }

    @Override
    public AlarmNotificationProfile createNewProfile(
            GatewayContext gatewayContext,
            DecodedResource<ExtensionPointConfig<AlarmNotificationProfileConfig, ?>> decodedResource,
            SlackNotificationProfileResource profileResource) throws Exception {
        return new SlackNotificationProfile(gatewayContext, decodedResource, profileResource);
    }

    @Override
    protected void validate(SlackNotificationProfileResource settings, ValidationErrors.Builder errors) {
        /*
         Optionally add validation to an incoming configuration object
         These error messages will be conveyed back to the standard web UI automatically
        */
        // errors.requireNotNull("someField", settings.auditProfileName());
        super.validate(settings, errors);
    }

    @Override
    public Optional<WebUiComponent> getWebUiComponent(ComponentType type) {
        return Optional.of(
            new ExtensionPointResourceForm(
                AlarmNotificationProfileConfig.RESOURCE_TYPE,
                "Alarm Notification Profile",
                TYPE_ID,
                SchemaUtil.fromType(AlarmNotificationProfileConfig.class),
                SchemaUtil.fromType(SlackNotificationProfileResource.class)
            )
        );
    }
}