package com.inductiveautomation.ignition.examples.ne.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.gateway.audit.AuditProfileRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.Category;
import com.inductiveautomation.ignition.gateway.localdb.persistence.EnumField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IdentityField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.LongField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;

public class ConsoleNotificationProfileSettings extends PersistentRecord{

	public static final RecordMeta<ConsoleNotificationProfileSettings> META = 
			new RecordMeta<ConsoleNotificationProfileSettings>(
					ConsoleNotificationProfileSettings.class,
					"ConsoleNotificationProfileSettings");
	public static final IdentityField Id = new IdentityField(META);
	public static final LongField ProfileId = new LongField(META, "ProfileId");
	public static final ReferenceField<AlarmNotificationProfileRecord> Profile = new ReferenceField<AlarmNotificationProfileRecord>(
			META,
			AlarmNotificationProfileRecord.META,
			"Profile",
			ProfileId);
	
	//we put our only setting specific to our profile type here
	//this field is just a field that will be used to only log notification to the console of
	//alarms who have at least this log level
	public static final EnumField<LogLevel> Level = new EnumField<LogLevel>(META, "MinLevel", LogLevel.class);
	
	//for optional auditing
	public static final LongField AuditProfileId = new LongField(META, "AuditProfileId");
	public static final ReferenceField<AuditProfileRecord> AuditProfile = new ReferenceField<AuditProfileRecord>(
			META, AuditProfileRecord.META, "AuditProfile", AuditProfileId);
	
	
	static final Category Settings = new Category("ConsoleNotificationProfileSettings.Category.Settings", 1)
	.include(Level);
	static final Category Auditing = new Category("ConsoleNotificationProfileSettings.Category.Auditing", 2)
	.include(AuditProfile);
	
	static{
		Profile.getFormMeta().setVisible(false);
	}
	
	@Override
	public RecordMeta<?> getMeta() {
		return META;
	}
	
	public LogLevel getLevel(){
		return getEnum(Level);
	}
	
	public String getAuditProfileName() {
		AuditProfileRecord rec = findReference(AuditProfile);
		return rec == null ? null : rec.getName();
	}

}
