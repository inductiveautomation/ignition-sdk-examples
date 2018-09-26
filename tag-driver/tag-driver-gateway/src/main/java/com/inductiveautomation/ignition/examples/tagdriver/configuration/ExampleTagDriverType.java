package com.inductiveautomation.ignition.examples.tagdriver.configuration;


import com.inductiveautomation.ignition.examples.tagdriver.ExampleTagDriver;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.settings.ExampleTagDriverSettings;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceSettingsRecord;
import com.inductiveautomation.xopc.driver.api.Driver;
import com.inductiveautomation.xopc.driver.api.DriverContext;
import com.inductiveautomation.xopc.driver.api.configuration.DriverType;
import org.jetbrains.annotations.NotNull;


public class ExampleTagDriverType extends DriverType {

    public static final String TYPE_ID = "ATDExample";

    public ExampleTagDriverType() {
        /* DisplayName and Description are retrieved from ADTExampleDriver.properties */
        super(TYPE_ID, "ExampleTagDriver.Meta.DisplayName", "ExampleTagDriver.Meta.Description");
    }

    @Override
    public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
        return ExampleTagDriverSettings.META;
    }

    @NotNull
    public Driver createDriver(DriverContext driverContext, DeviceSettingsRecord deviceSettings) {
        ExampleTagDriverSettings settings =
                findProfileSettingsRecord(driverContext.getGatewayContext(), deviceSettings);

        return new ExampleTagDriver(driverContext, settings);
    }

    @Override
    public ReferenceField<?> getSettingsRecordForeignKey() {
        return ExampleTagDriverSettings.DEVICE_SETTINGS;
    }

}
