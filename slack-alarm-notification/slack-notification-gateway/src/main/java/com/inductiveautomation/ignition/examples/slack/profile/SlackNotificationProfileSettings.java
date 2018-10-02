package com.inductiveautomation.ignition.examples.slack.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.gateway.audit.AuditProfileRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.Category;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IdentityField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.LongField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.StringField;
import org.apache.wicket.markup.html.form.TextField;

public class SlackNotificationProfileSettings extends PersistentRecord{

	public static final RecordMeta<SlackNotificationProfileSettings> META =
        new RecordMeta<>(
            SlackNotificationProfileSettings.class,
            "SlackNotificationProfileSettings"
        );
	public static final IdentityField Id = new IdentityField(META);
	public static final LongField ProfileId = new LongField(META, "ProfileId");
	public static final ReferenceField<AlarmNotificationProfileRecord> Profile = new ReferenceField<>(
			META,
			AlarmNotificationProfileRecord.META,
			"Profile",
			ProfileId);
	
	//for optional auditing
	public static final LongField AuditProfileId = new LongField(META, "AuditProfileId");
	public static final ReferenceField<AuditProfileRecord> AuditProfile = new ReferenceField<>(
			META, AuditProfileRecord.META, "AuditProfile", AuditProfileId);

	static final Category Auditing = new Category("SlackNotificationProfileSettings.Category.Auditing", 1)
	.include(AuditProfile);
	
	static{
		Profile.getFormMeta().setVisible(false);
	}
	
	@Override
	public RecordMeta<?> getMeta() {
		return META;
	}

	public String getAuditProfileName() {
		AuditProfileRecord rec = findReference(AuditProfile);
		return rec == null ? null : rec.getName();
	}

}
