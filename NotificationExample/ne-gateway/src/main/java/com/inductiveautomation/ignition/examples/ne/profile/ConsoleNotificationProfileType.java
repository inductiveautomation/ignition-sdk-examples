package com.inductiveautomation.ignition.examples.ne.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfile;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileType;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class ConsoleNotificationProfileType extends AlarmNotificationProfileType{

	public static final String TYPE_ID = "ConsoleType";
	public static final ContactType CONSOLE = new ContactType("console", new LocalizedString("ConsoleNotification.ContactType.console"));
	
	
	public ConsoleNotificationProfileType() {
		super(TYPE_ID,
				"ConsoleNotification." + "ConsoleNotificationProfileType.Name",
				"ConsoleNotification." + "ConsoleNotificationProfileType.Description");
	}
	
	@Override
	public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
		return ConsoleNotificationProfileSettings.META;
	}

	@Override
	public AlarmNotificationProfile createNewProfile(GatewayContext context,
			AlarmNotificationProfileRecord profileRecord) throws Exception {
		ConsoleNotificationProfileSettings settings = findProfileSettingsRecord(context, profileRecord);
		
		if (settings == null) {
			throw new Exception(
					String.format("Couldn't find settings record for profile '%s'.", profileRecord.getName()));
		}

		return new ConsoleNotificationProfile(context, profileRecord, settings);
	}

}
