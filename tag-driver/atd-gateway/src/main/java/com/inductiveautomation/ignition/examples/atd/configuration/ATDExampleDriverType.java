package com.inductiveautomation.ignition.examples.atd.configuration;


import com.inductiveautomation.ignition.examples.atd.ATDExampleDriver;
import com.inductiveautomation.ignition.examples.atd.configuration.settings.ATDExampleDriverSettings;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.opcua.server.api.Device;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceSettingsRecord;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceType;
import org.jetbrains.annotations.NotNull;


public class ATDExampleDriverType extends DeviceType {

    public static final String TYPE_ID = "ATDExample";

    public ATDExampleDriverType() {
        /* DisplayName and Description are retrieved from ADTExampleDriver.properties */
        super(TYPE_ID, "ATDExampleDriver.Meta.DisplayName", "ATDExampleDriver.Meta.Description");
    }

    @Override
    public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
        return ATDExampleDriverSettings.META;
    }

    @NotNull
    @Override
    public Device createDevice(DeviceContext deviceContext, DeviceSettingsRecord deviceSettings) {
        ATDExampleDriverSettings settings =
                findProfileSettingsRecord(deviceContext.getGatewayContext(), deviceSettings);

        return new ATDExampleDriver(deviceContext, settings);
    }

    @Override
    public ReferenceField<?> getSettingsRecordForeignKey() {
        return ATDExampleDriverSettings.DEVICE_SETTINGS;
    }

}
