package com.inductiveautomation.ignition.examples.atd.configuration;


import com.inductiveautomation.ignition.examples.atd.ATDExampleDriver;
import com.inductiveautomation.ignition.examples.atd.configuration.settings.ATDExampleDriverSettings;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.xopc.driver.api.Driver;
import com.inductiveautomation.xopc.driver.api.DriverContext;
import com.inductiveautomation.xopc.driver.api.configuration.DeviceSettingsRecord;
import com.inductiveautomation.xopc.driver.api.configuration.DriverType;


public class ATDExampleDriverType extends DriverType {

    public static final String TYPE_ID = "ATDExample";

    public ATDExampleDriverType() {
        /* DisplayName and Description are retrieved from ADTExampleDriver.properties */
        super(TYPE_ID, "ATDExampleDriver.Meta.DisplayName", "ATDExampleDriver.Meta.Description");
    }

    @Override
    public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
        return ATDExampleDriverSettings.META;
    }

    @Override
    public Driver createDriver(DriverContext driverContext, DeviceSettingsRecord deviceSettings) {
        ATDExampleDriverSettings settings =
                findProfileSettingsRecord(driverContext.getGatewayContext(), deviceSettings);

        return new ATDExampleDriver(driverContext, settings);
    }

    @Override
    public ReferenceField<?> getSettingsRecordForeignKey() {
        return ATDExampleDriverSettings.DEVICE_SETTINGS;
    }

}
