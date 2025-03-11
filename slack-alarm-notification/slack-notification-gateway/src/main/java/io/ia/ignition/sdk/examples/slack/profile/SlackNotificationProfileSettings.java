package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.gateway.audit.AuditProfileRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.*;

@SuppressWarnings("deprecation")
public class SlackNotificationProfileSettings extends PersistentRecord {

    public static final RecordMeta<SlackNotificationProfileSettings> META =
            new RecordMeta<>(SlackNotificationProfileSettings.class, "SlackNotificationProfileSettings");
    public static final IdentityField Id = new IdentityField(META);
    public static final LongField ProfileId = new LongField(META, "ProfileId");
    public static final ReferenceField<AlarmNotificationProfileRecord> Profile =
            new ReferenceField<>(META, AlarmNotificationProfileRecord.META, "Profile", ProfileId);

    public static final LongField AuditProfileId = new LongField(META, "AuditProfileId");
    public static final ReferenceField<AuditProfileRecord> AuditProfile =
            new ReferenceField<>(META, AuditProfileRecord.META, "AuditProfile", AuditProfileId);

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }

    public String getAuditProfileName() {
        AuditProfileRecord rec = findReference(AuditProfile);
        return rec == null ? null : rec.getName();
    }

}
