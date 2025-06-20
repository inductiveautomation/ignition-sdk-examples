package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.alarming.common.notification.NotificationProfileProperty;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfile;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileConfig;
import com.inductiveautomation.ignition.alarming.notification.NotificationContext;
import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.WellKnownPathTypes;
import com.inductiveautomation.ignition.common.alarming.AlarmEvent;
import com.inductiveautomation.ignition.common.audit.AuditRecord;
import com.inductiveautomation.ignition.common.config.FallbackPropertyResolver;
import com.inductiveautomation.ignition.common.expressions.parsing.Parser;
import com.inductiveautomation.ignition.common.expressions.parsing.StringParser;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataQuality;
import com.inductiveautomation.ignition.common.user.ContactInfo;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.audit.AuditProfile;
import com.inductiveautomation.ignition.gateway.audit.AuditRecordBuilder;
import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.expressions.AlarmEventCollectionExpressionParseContext;
import com.inductiveautomation.ignition.gateway.expressions.FormattedExpressionParseContext;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.ProfileStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static io.ia.ignition.sdk.examples.slack.profile.SlackNotificationExtensionPoint.SLACK_WEBHOOK;

public class SlackNotificationProfile implements AlarmNotificationProfile {

    private final GatewayContext context;
    private final String auditProfileName;
    private final String profileName;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private volatile ProfileStatus profileStatus = ProfileStatus.UNKNOWN;

    private final LoggerEx log = LoggerEx.newBuilder().build(getClass());

    private Map<String, String> contextMap;

    public SlackNotificationProfile(GatewayContext context,
                                    DecodedResource<ExtensionPointConfig<AlarmNotificationProfileConfig, ?>> profileRecord,
                                    SlackNotificationProfileResource settingsRecord) {
        this.context = context;
        this.profileName = profileRecord.name();
        this.auditProfileName = settingsRecord.auditProfileName();
    }

    @Override
    public String getName() {
        return profileName;
    }

    @Override
    public String getProfileType() {
        return SlackNotificationExtensionPoint.TYPE_ID;
    }

    @Override
    public Collection<NotificationProfileProperty<?>> getProperties() {
        return List.of(
                SlackProperties.MESSAGE,
                SlackProperties.THROTTLED_MESSAGE,
                SlackProperties.TEST_MODE
        );
    }

    @Override
    public ProfileStatus getStatus() {
        return profileStatus;
    }

    @Override
    public Collection<ContactType> getSupportedContactTypes() {
        return List.of(SLACK_WEBHOOK);
    }

    @Override
    public void onShutdown() {
        executor.shutdown();
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
        this.contextMap = contextMap;
    }

    @Override
    public void onStartup() {
        profileStatus = ProfileStatus.RUNNING;
    }

    @Override
    public void sendNotification(final NotificationContext notificationContext) {
        executor.execute(() -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            } else {
                MDC.clear();
            }

            var slackContactInfos = notificationContext.getUser().getContactInfo().stream()
                    .filter(contactInfo -> contactInfo.getContactType().equals(SLACK_WEBHOOK.getContactType()))
                    .toList();

            String message = evaluateMessageExpression(notificationContext);

            boolean testMode = notificationContext.getNonNull(SlackProperties.TEST_MODE, true);
            boolean success = true;
            if (testMode) {
                log.infof(
                        """
                                THIS PROFILE IS RUNNING IN TEST MODE. The following WOULD have been sent:
                                Message: %s""",
                        message
                );

                notificationContext.notificationDone();
                return;
            }

            try {
                var client = HttpClient.newHttpClient();
                for (ContactInfo slackContactInfo : slackContactInfos) {
                    String url = slackContactInfo.getValue();

                    log.debugf(
                            "Attempting to send an alarm notification to %s via %s",
                            notificationContext.getUser(),
                            url
                    );

                    HttpResponse<String> response = sendNotification(client, url, message);
                    if (response.statusCode() != 200) {
                        log.errorf(
                                "Failed to send notification to %s via %s. Response code: %d",
                                notificationContext.getUser(),
                                url,
                                response.statusCode()
                        );
                        success = false;
                    } else {
                        log.tracef(
                                "Successfully sent notification to %s via %s",
                                notificationContext.getUser(),
                                url
                        );
                    }

                    audit(notificationContext, success);
                }
            } catch (Exception e) {
                log.error("Unable to send notification", e);
            }

            notificationContext.notificationDone();
        });

    }

    private HttpResponse<String> sendNotification(HttpClient client, String url, String message)
            throws URISyntaxException, IOException, InterruptedException {
        JsonObject json = new JsonObject();
        json.addProperty("text", message);

        var request = HttpRequest.newBuilder(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .header("Content-Type", "application/json")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void audit(NotificationContext notificationContext, boolean success) {
        if (!StringUtils.isBlank(auditProfileName)) {
            try {
                AuditProfile p = context.getAuditManager().getProfile(auditProfileName);
                if (p == null) {
                    return;
                }
                for (AlarmEvent event : notificationContext.getAlarmEvents()) {
                    AuditRecord r = new AuditRecordBuilder()
                            .setAction("Slack Message")
                            .setActionTarget(
                                    event.getSource().extend(WellKnownPathTypes.Event, event.getId().toString())
                                            .toString())
                            .setActionValue(success ? "SUCCESS" : "FAILURE")
                            .setActor(notificationContext.getUser().getPath().toString())
                            .setActorHost(profileName)
                            .setOriginatingContext(ApplicationScope.GATEWAY)
                            .setOriginatingSystem("Alarming")
                            .setStatusCode(success ? DataQuality.GOOD_DATA.getIntValue() : 0)
                            .setTimestamp(new Date())
                            .build();
                    p.audit(r);
                }
            } catch (Exception e) {
                log.error("Error auditing Slack event.", e);
            }
        }
    }

    private String evaluateMessageExpression(NotificationContext notificationContext) {
        Parser parser = new StringParser();

        FallbackPropertyResolver resolver =
                new FallbackPropertyResolver(context.getAlarmManager().getPropertyResolver());

        FormattedExpressionParseContext parseContext =
                new FormattedExpressionParseContext(
                        new AlarmEventCollectionExpressionParseContext(resolver, notificationContext.getAlarmEvents()));

        String expressionString;
        String customMessage = notificationContext.getAlarmEvents().get(0).get(SlackProperties.CUSTOM_MESSAGE);
        boolean isThrottled = notificationContext.getAlarmEvents().size() > 1;

        if (isThrottled || StringUtils.isBlank(customMessage)) {
            expressionString = isThrottled ?
                    notificationContext.getOrDefault(SlackProperties.THROTTLED_MESSAGE) :
                    notificationContext.getOrDefault(SlackProperties.MESSAGE);
        } else {
            expressionString = customMessage;
        }

        String evaluated = expressionString;
        try {
            QualifiedValue value = parser.parse(expressionString, parseContext).execute();
            if (value.getQuality().isGood()) {
                evaluated = TypeUtilities.toString(value.getValue());
            }
        } catch (Exception e) {
            log.errorf("Error parsing expression '%s'.", expressionString, e);
        }

        log.tracef("Message evaluated to '%s'.", evaluated);

        return evaluated;
    }
}
