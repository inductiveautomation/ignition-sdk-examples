package com.inductiveautomation.ignition.examples.tagdriver;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.ExampleDeviceType;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.opcua.server.api.ExtensionManager;

public class ModuleHook extends AbstractGatewayModuleHook {
    private ExtensionManager extensionManager;
    private ExampleDeviceType deviceType;

    @Override
    public void setup(GatewayContext gatewayContext) {
        BundleUtil.get().addBundle(ExampleTagDriver.class);
        extensionManager = gatewayContext.getModuleServicesManager().getService(ExtensionManager.class);
        deviceType = new ExampleDeviceType();
    }

    @Override
    public void startup(LicenseState licenseState) {
        if (extensionManager != null) {
            extensionManager.registerDeviceType(deviceType);
        }
    }

    @Override
    public void shutdown() {
        BundleUtil.get().removeBundle(ExampleTagDriver.class);
        if (extensionManager != null) {
            extensionManager.unregisterDeviceType(deviceType);
        }
    }
}
