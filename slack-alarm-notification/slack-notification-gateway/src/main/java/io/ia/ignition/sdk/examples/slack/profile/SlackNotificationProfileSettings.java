package io.ia.ignition.sdk.examples.slack.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.gateway.audit.AuditProfileRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.*;

/**
 * This is our "legacy" configuration, only kept around for backwards compatibility.
 * Obviously this doesn't really matter for this SDK example, but in a real production module you'll want to preserve
 * configuration data from 8.1 to 8.3 smoothly, and to do that you <b>must</b> keep the old ORM record around.
 * <p/>
 * When the next major version is released, upgrades will only have to be possible from 8.3, so this old code can be
 * dropped then.
 */
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
