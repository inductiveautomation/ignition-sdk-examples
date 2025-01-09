package com.inductiveautomation.ignition.examples.tagdriver.settings;

import com.inductiveautomation.ignition.gateway.localdb.persistence.IntField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.LongField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceSettingsRecord;
import java.io.Serial;
import simpleorm.dataset.SFieldFlags;

/**
 * Implements all functionality needed to save a device and its settings in the internal database.
 */
@Deprecated(since = "8.3.0")
@SuppressWarnings("unused")
public class ExampleDeviceSettings extends PersistentRecord {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Needed so that the device record can be saved in the internal database.
   */
  public static final RecordMeta<ExampleDeviceSettings> META =
      new RecordMeta<>(ExampleDeviceSettings.class, "ExampleDeviceSettings");

  /**
   * Reference to parent DeviceSettingsRecord: holds items like Device Name setting and Enabled
   * setting. These fields also appear in the General category section when creating a new driver in
   * the Gateway.
   */
  public static final LongField DEVICE_SETTINGS_ID =
      new LongField(META, "DeviceSettingsId", SFieldFlags.SPRIMARY_KEY);

  /**
   * Needed to link a device settings record to the device record in the internal database.
   */
  public static final ReferenceField<DeviceSettingsRecord> DEVICE_SETTINGS = new ReferenceField<>(
      META,
      DeviceSettingsRecord.META,
      "DeviceSettings",
      DEVICE_SETTINGS_ID
  );

  /**
   * Settings specific to the ExampleDevice; each one must be placed in a Category.
   */
  public static final IntField TAG_COUNT = new IntField(META, "TagCount", SFieldFlags.SMANDATORY);

  @Override
  public RecordMeta<?> getMeta() {
    return META;
  }

}
