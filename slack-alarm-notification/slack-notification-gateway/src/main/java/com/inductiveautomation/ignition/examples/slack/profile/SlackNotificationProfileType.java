package com.inductiveautomation.ignition.examples.slack.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfile;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileType;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class SlackNotificationProfileType extends AlarmNotificationProfileType{
    public static final String TYPE_ID = "SlackType";
    public static final ContactType SLACK =
        new ContactType("Slack", new LocalizedString("SlackNotification.ContactType.Slack"));

	public SlackNotificationProfileType() {
		super(TYPE_ID,
				"SlackNotification." + "SlackNotificationProfileType.Name",
				"SlackNotification." + "SlackNotificationProfileType.Description");
	}
	
	@Override
	public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
		return SlackNotificationProfileSettings.META;
	}

	@Override
	public AlarmNotificationProfile createNewProfile(GatewayContext context,
			AlarmNotificationProfileRecord profileRecord) throws Exception {
		SlackNotificationProfileSettings settings = findProfileSettingsRecord(context, profileRecord);
		
		if (settings == null) {
			throw new Exception(
					String.format("Couldn't find settings record for profile '%s'.", profileRecord.getName()));
		}

		return new SlackNotificationProfile(context, profileRecord, settings);
	}

}
