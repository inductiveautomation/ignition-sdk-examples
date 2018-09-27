package com.inductiveautomation.ignition.examples.tagdriver.configuration;


import com.inductiveautomation.ignition.examples.tagdriver.ExampleTagDriver;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.settings.ExampleDeviceSettings;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.opcua.server.api.Device;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceSettingsRecord;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceType;
import org.jetbrains.annotations.NotNull;


public class ExampleDeviceType extends DeviceType {

    public static final String TYPE_ID = "ATDExample";

    public ExampleDeviceType() {
        /* DisplayName and Description are retrieved from ADTExampleDriver.properties */
        super(TYPE_ID, "ExampleTagDriver.Meta.DisplayName", "ExampleTagDriver.Meta.Description");
    }

    @Override
    public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
        return ExampleDeviceSettings.META;
    }

    @Override
    public ReferenceField<?> getSettingsRecordForeignKey() {
        return ExampleDeviceSettings.DEVICE_SETTINGS;
    }

    @NotNull
    @Override
    public Device createDevice(@NotNull DeviceContext deviceContext,
                               @NotNull DeviceSettingsRecord deviceSettingsRecord) {
        return new ExampleTagDriver(deviceContext, deviceSettingsRecord);
    }
}
