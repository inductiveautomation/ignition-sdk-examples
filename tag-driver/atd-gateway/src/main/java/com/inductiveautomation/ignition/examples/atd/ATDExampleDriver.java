package com.inductiveautomation.ignition.examples.atd;

import java.util.Random;

import com.inductiveautomation.ignition.examples.atd.configuration.settings.ATDExampleDriverSettings;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;
import com.inductiveautomation.xopc.driver.api.AbstractDriver;
import com.inductiveautomation.xopc.driver.api.AbstractTagDriver;
import com.inductiveautomation.xopc.driver.api.DriverContext;
import com.inductiveautomation.xopc.driver.api.tags.DriverTag;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public class ATDExampleDriver extends AbstractTagDriver {

    /**
     * Creates some tags that can be referenced when the driver is running.
     *
     * @param deviceContext
     * @param settings
     */
    public ATDExampleDriver(DeviceContext deviceContext, ATDExampleDriverSettings settings) {
        super(deviceContext);

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
                public BuiltinDataType getDataType() {
                    return BuiltinDataType.Int32;
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
