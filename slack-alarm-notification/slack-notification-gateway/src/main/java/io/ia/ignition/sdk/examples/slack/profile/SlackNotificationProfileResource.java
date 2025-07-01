package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.*;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;

import javax.annotation.Nullable;

public record SlackNotificationProfileResource(
        @Nullable
        @FormCategory("SLACK SETTINGS")
        @Label("Audit Profile")
        @FormField(FormFieldType.REFERENCE)
        @FormReferenceType("ignition/audit-profile")
        @IsNullable
        @DefaultValue("null")
        @DescriptionKey("SlackNotificationProfileSettings.AuditProfileName.Description")
        String auditProfileName
) {
}
