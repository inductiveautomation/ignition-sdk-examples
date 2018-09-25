package com.inductiveautomation.ignition.examples.consolenotification.profile;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.inductiveautomation.ignition.alarming.common.notification.NotificationProfileProperty;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfile;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.alarming.notification.NotificationContext;
import com.inductiveautomation.ignition.gateway.model.ProfileStatus;
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
import com.inductiveautomation.ignition.gateway.expressions.AlarmEventCollectionExpressionParseContext;
import com.inductiveautomation.ignition.gateway.expressions.FormattedExpressionParseContext;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistenceSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class ConsoleNotificationProfile implements AlarmNotificationProfile {

	private final GatewayContext context;
	private final AlarmNotificationProfileRecord profileRecord;
	private final ConsoleNotificationProfileSettings settingsRecord;
	private String auditProfileName, profileName;
	private final ScheduledExecutorService executor;
	private volatile ProfileStatus profileStatus = ProfileStatus.UNKNOWN;

	private final LoggerEx log = new LoggerEx(Logger.getLogger(getClass()));

	ConsoleNotificationProfile(final GatewayContext context,
							   final AlarmNotificationProfileRecord profileRecord,
							   final ConsoleNotificationProfileSettings settingsRecord) {
		this.context = context;
		this.profileRecord = profileRecord;
		this.settingsRecord = settingsRecord;
		this.executor = Executors.newSingleThreadScheduledExecutor();
		profileName = profileRecord.getName();

		//We need to retrieve the audit profile name
		try (PersistenceSession session = context.getPersistenceInterface().getSession(settingsRecord.getDataSet())) {
			//getAuditProfileName gets the AuditProfileRecord and queries the name
			// So, we don't know if the AuditProfileRecord is detached so must reattach
			auditProfileName = settingsRecord.getAuditProfileName();
		} catch (Exception e) {
			log.error("Error retrieving audit profile name.", e);
		}

	}

	@Override
	public String getName() {
		return profileRecord.getName();
	}

	@Override
	public Collection<NotificationProfileProperty<?>> getProperties() {
		return Lists.newArrayList(
				ConsoleLogProperties.MESSAGE,
				ConsoleLogProperties.THROTTLED_MESSAGE,
				ConsoleLogProperties.TEST_MODE);
	}

	@Override
	public ProfileStatus getStatus() {
		return profileStatus;
	}

	@Override
	public Collection<ContactType> getSupportedContactTypes() {
		return Lists.newArrayList(ConsoleNotificationProfileType.CONSOLE);
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
			Collection<ContactInfo> consoleContactInfos =
					Collections2.filter(notificationContext.getUser().getContactInfo(), new IsConsoleContactInfo());

			String message = evaluateMessageExpression(notificationContext);
			// Check if we're in 'test mode'
			//not really useful because we're logging, but just demonstrates would be done if using another
			//form of notification
			boolean testMode = notificationContext.getOrDefault(ConsoleLogProperties.TEST_MODE);
			if (testMode) {
				log.infof("THIS PROFILE IS RUNNING IN TEST MODE. The following WOULD have been sent:\n" +
						"Recipient(s): %s\n" +
						"Message: %s",
						consoleContactInfos, message);

				notificationContext.notificationDone();
				return;
			}

			LogLevel level = settingsRecord.getLevel();

			String formatString = "Recipient(s): %s\n" + "Message: %s";
			//log to all the contact infos
			level.log(log, consoleContactInfos, formatString, message);
			//just shows how you can audit notifications optionally
			audit(true, "Logged to Console", notificationContext);

			notificationContext.notificationDone();
		});

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
				log.error("Error auditing email event.", e);
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
		String customMessage = notificationContext.getAlarmEvents().get(0).get(ConsoleLogProperties.CUSTOM_MESSAGE);
		boolean isThrottled = notificationContext.getAlarmEvents().size() > 1;

		if (isThrottled || StringUtils.isBlank(customMessage)) {
			expressionString = isThrottled ?
					notificationContext.getOrDefault(ConsoleLogProperties.THROTTLED_MESSAGE) :
					notificationContext.getOrDefault(ConsoleLogProperties.MESSAGE);
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
	private static class IsConsoleContactInfo implements Predicate<ContactInfo> {
		@Override
		public boolean apply(ContactInfo contactInfo) {
			return ConsoleNotificationProfileType.CONSOLE.getContactType().equals(contactInfo.getContactType());
		}
	}

}
