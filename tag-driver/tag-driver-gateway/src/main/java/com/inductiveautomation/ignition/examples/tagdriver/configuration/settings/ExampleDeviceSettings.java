package com.inductiveautomation.ignition.examples.tagdriver.configuration.settings;

import com.inductiveautomation.ignition.gateway.localdb.persistence.Category;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IntField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.LongField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceSettingsRecord;
import simpleorm.dataset.SFieldFlags;

/**
 * Implements all functionality needed to save a device and its settings in the internal database.
 */
public class ExampleDeviceSettings extends PersistentRecord {

    private static final long serialVersionUID = 1L;

    /**
     * Needed so that the device record can be saved in the internal database.
     */
    public static final RecordMeta<ExampleDeviceSettings> META =
            new RecordMeta<>(ExampleDeviceSettings.class, "ExampleDeviceSettings");

    /**
     * Reference to parent DeviceSettingsRecord: holds items like Device Name setting and Enabled setting.
     * These fields also appear in the General category section when creating a new driver in the Gateway.
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

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }

    /**
     * Settings specific to the ExampleTagDriver; each one must be placed in a Category.
     */
    public static final IntField TAG_COUNT = new IntField(META, "TagCount", SFieldFlags.SMANDATORY);

    /**
     * Categories specific to the ExampleTagDriver; each category appears below the General category in the Gateway
     * when creating a new driver.
     * <p>
     * In this case, the displayKey below is referencing ExampleDeviceSettings.properties, which should be located
     * in the same package as the class file on the file system. You must put the actual category name into this file.
     * <p>
     * The order number determines the order in which multiple categories are displayed on the page.
     */
    public static final Category EXAMPLE_CATEGORY =
            new Category("ExampleDeviceSettings.ExampleCategory", 1001).include(TAG_COUNT);

    static {
        // Hides some generic ReferenceField settings that are not needed in our driver example.
        DEVICE_SETTINGS.getFormMeta().setVisible(false);
    }

    /**
     * Get the number of tags that will be exposed to the driver
     *
     * @return an int with the saved tag count
     */
    public int getTagCount() {
        return getInt(TAG_COUNT);
    }

    /**
     * Set the number of tags that will be exposed to the driver
     *
     * @param tagCount an int that represents the tag count to save
     */
    public void setTagCount(int tagCount) {
        setInt(TAG_COUNT, tagCount);
    }

}
