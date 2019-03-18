package com.inductiveautomation.ignition.examples.slack.profile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.net.ssl.HttpsURLConnection;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.inductiveautomation.ignition.alarming.common.notification.NotificationProfileProperty;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfile;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.alarming.notification.NotificationContext;
import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.WellKnownPathTypes;
import com.inductiveautomation.ignition.common.alarming.AlarmEvent;
import com.inductiveautomation.ignition.common.config.FallbackPropertyResolver;
import com.inductiveautomation.ignition.common.expressions.parsing.Parser;
import com.inductiveautomation.ignition.common.expressions.parsing.StringParser;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataQuality;
import com.inductiveautomation.ignition.common.user.ContactInfo;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.audit.AuditProfile;
import com.inductiveautomation.ignition.gateway.audit.AuditRecord;
import com.inductiveautomation.ignition.gateway.audit.AuditRecordBuilder;
import com.inductiveautomation.ignition.gateway.dataroutes.HttpMethod;
import com.inductiveautomation.ignition.gateway.expressions.AlarmEventCollectionExpressionParseContext;
import com.inductiveautomation.ignition.gateway.expressions.FormattedExpressionParseContext;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistenceSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.ProfileStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

public class SlackNotificationProfile implements AlarmNotificationProfile {

	private final GatewayContext context;
	private final AlarmNotificationProfileRecord profileRecord;
    private String auditProfileName, profileName;
	private final ScheduledExecutorService executor;
	private volatile ProfileStatus profileStatus = ProfileStatus.UNKNOWN;

	private final LoggerEx log = new LoggerEx(Logger.getLogger(getClass()));

	public SlackNotificationProfile(final GatewayContext context,
									final AlarmNotificationProfileRecord profileRecord,
									final SlackNotificationProfileSettings settingsRecord) {
		this.context = context;
		this.profileRecord = profileRecord;
        this.executor = Executors.newSingleThreadScheduledExecutor();
		profileName = profileRecord.getName();

		//We need to retrieve the audit profile name
		try (PersistenceSession session = context.getPersistenceInterface().getSession(settingsRecord.getDataSet())) {
			//getAuditProfileName gets the AuditProfileRecord and queries the name
			// So, we don't know if the AuditProfileRecord is detached so must reattach
			auditProfileName = settingsRecord.getAuditProfileName();
		} catch (Exception e) {
			log.error("Error retrieving notification profile details.", e);
		}

	}

	@Override
	public String getName() {
		return profileRecord.getName();
	}

	@Override
	public Collection<NotificationProfileProperty<?>> getProperties() {
        return Lists.newArrayList(
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
		return Lists.newArrayList(SlackNotificationProfileType.SLACK);
	}

	@Override
	public void onShutdown() {
		executor.shutdown();
	}

	@Override
	public void onStartup() {
		profileStatus = ProfileStatus.RUNNING;
	}

	@Override
	public void sendNotification(final NotificationContext notificationContext) {
		executor.execute(() -> {
			Collection<ContactInfo> slackContactInfos =
					Collections2.filter(notificationContext.getUser().getContactInfo(), new IsSlackContactInfo());

			String message = evaluateMessageExpression(notificationContext);
            JsonObject json = new JsonObject();
            json.addProperty("text", message);
			// Check if we're in 'test mode'
			boolean testMode = notificationContext.getOrDefault(SlackProperties.TEST_MODE);
			boolean success = true;
			if (testMode) {
                log.infof(
                    "THIS PROFILE IS RUNNING IN TEST MODE. The following WOULD have been sent:\nMessage: %s",
                    message
                );

				notificationContext.notificationDone();
				return;
			}

            try( CloseableHttpClient httpClient = HttpClientBuilder.create().build() ) {
                for (ContactInfo slackContactInfo : slackContactInfos) {
                    String url = slackContactInfo.getValue();
                    log.debugf(
                        "Attempting to send an alarm notification to %s via %s",
                        notificationContext.getUser(),
                        url
                    );
                    HttpPost request = new HttpPost(url);
                    try {
                        StringEntity params = new StringEntity(json.toString());
                        request.addHeader("Content-Type", "application/json");
                        request.setEntity(params);
                        httpClient.execute(request);
                    } catch (IOException e) {
                        log.error("Unable to send notification", e);
                        success = false;
                    }
                    //just shows how you can audit notifications optionally
                    audit(success, "Slack Message", notificationContext);
                }
            } catch (IOException ex) {
                log.error("Unable to send notification", ex);
            }

			notificationContext.notificationDone();
		});

	}

    private void writeRequest(HttpURLConnection con, String data) throws IOException {
        try (Writer writer = new OutputStreamWriter(con.getOutputStream())) {
            writer.write(StringUtils.defaultString(data));
        }
    }

	private void audit(boolean success, String eventDesc, NotificationContext notificationContext) {
		if (!StringUtils.isBlank(auditProfileName)) {
			try {
				AuditProfile p = context.getAuditManager().getProfile(auditProfileName);
				if (p == null) {
					return;
				}
				List<AlarmEvent> alarmEvents = notificationContext.getAlarmEvents();
				for (AlarmEvent event : alarmEvents) {
					AuditRecord r = new AuditRecordBuilder()
							.setAction(eventDesc)
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

	/**
	 * A {@link Predicate} that returns true if a {@link ContactInfo}'s {@link ContactType} is Console.
	 */
	private static class IsSlackContactInfo implements Predicate<ContactInfo> {
		@Override
		public boolean apply(ContactInfo contactInfo) {
			return SlackNotificationProfileType.SLACK.getContactType().equals(contactInfo.getContactType());
		}
	}

}
