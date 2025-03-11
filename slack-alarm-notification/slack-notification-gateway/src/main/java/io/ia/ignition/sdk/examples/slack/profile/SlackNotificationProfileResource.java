package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DescriptionKey;

import javax.annotation.Nullable;

public record SlackNotificationProfileResource(
        @DescriptionKey("SlackNotificationProfileSettings.AuditProfileName.Description")
        @Nullable
        String auditProfileName
) {
}
