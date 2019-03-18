package com.inductiveautomation.ignition.examples.tagdriver.configuration;


import com.inductiveautomation.ignition.examples.tagdriver.ExampleDevice;
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

    public static final String TYPE_ID = "UADriverExample";

    public ExampleDeviceType() {
        /* DisplayName and Description are retrieved from ExampleDevice.properties */
        super(TYPE_ID, "ExampleDevice.Meta.DisplayName", "ExampleDevice.Meta.Description");
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
        ExampleDeviceSettings settings =
            findProfileSettingsRecord(deviceContext.getGatewayContext(), deviceSettingsRecord);
        return new ExampleDevice(deviceContext, settings);
    }
}
