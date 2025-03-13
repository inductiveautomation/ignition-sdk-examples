package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.alarming.notification.*;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.gateway.audit.AuditProfileType;
import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.config.ReferenceFinderBuilder;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

import java.util.Objects;
import java.util.function.Consumer;


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
            new ContactType("Slack", new LocalizedString("SlackNotification.ContactType.slack"));

    public SlackNotificationExtensionPoint() {
        super(TYPE_ID,
                "SlackNotification.SlackNotificationProfileType.Name",
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
                        .match((config, auditProfile) -> Objects.equals(auditProfile, config.auditProfileName()))
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
        // Optionally add validation to an incoming configuration object
        // These error messages will be conveyed back to the standard web UI automatically
        // errors.requireNotNull("someField", settings.auditProfileName());
        super.validate(settings, errors);
    }
}