package com.inductiveautomation.ignition.examples.atd;

import java.util.Random;

import com.inductiveautomation.ignition.examples.atd.configuration.settings.ATDExampleDriverSettings;
import com.inductiveautomation.opcua.types.DataType;
import com.inductiveautomation.opcua.types.DataValue;
import com.inductiveautomation.opcua.types.Variant;
import com.inductiveautomation.xopc.driver.api.AbstractTagDriver;
import com.inductiveautomation.xopc.driver.api.DriverContext;
import com.inductiveautomation.xopc.driver.api.tags.DriverTag;

public class ATDExampleDriver extends AbstractTagDriver {

    /**
     * Creates some tags that can be referenced when the driver is running.
     *
     * @param driverContext
     * @param settings
     */
    public ATDExampleDriver(DriverContext driverContext, ATDExampleDriverSettings settings) {
        super(driverContext);

        int tagCount = settings.getTagCount();

        for (int i = 0; i < tagCount; i++) {
            final int tagNumber = i + 1;
            final int tagValue = new Random().nextInt();

            addDriverTag(new DriverTag() {
                @Override
                public DataValue getValue() {
                    return new DataValue(new Variant(tagValue));
                }

                @Override
                public DataType getDataType() {
                    return DataType.Int32;
                }

                @Override
                public String getAddress() {
                    return String.format("Path/To/Tag%s", tagNumber);
                }
            });
        }
    }


    @Override
    public String getDriverStatus() {
        return "Connected";
    }

}
