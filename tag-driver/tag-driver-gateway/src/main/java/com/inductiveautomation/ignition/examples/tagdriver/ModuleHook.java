package com.inductiveautomation.ignition.examples.tagdriver;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.ExampleTagDriverType;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.xopc.driver.api.DriverAPI;
import com.inductiveautomation.xopc.driver.api.configuration.DriverType;
import com.inductiveautomation.xopc.driver.common.AbstractDriverModuleHook;

public class ModuleHook extends AbstractDriverModuleHook {

    private static final ImmutableList<DriverType> DRIVER_TYPES;

    static {
        DRIVER_TYPES = ImmutableList.<DriverType>builder()
                .add(new ExampleTagDriverType())
                .build();
    }

    @Override
    public void setup(GatewayContext context) {
        BundleUtil.get().addBundle(ExampleTagDriver.class);

        super.setup(context);
    }

    @Override
    public void shutdown() {
        BundleUtil.get().removeBundle(ExampleTagDriver.class);

        super.shutdown();
    }

    @Override
    protected int getExpectedAPIVersion() {
        return DriverAPI.VERSION;
    }

    @Override
    protected List<DriverType> getDriverTypes() {
        return DRIVER_TYPES;
    }

}
