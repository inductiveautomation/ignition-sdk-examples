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
    private GatewayContext context;

    @Override
    public void setup(GatewayContext gatewayContext) {
        BundleUtil.get().addBundle(ExampleDevice.class);
        context = gatewayContext;
        deviceType = new ExampleDeviceType();
    }

    @Override
    public void startup(LicenseState licenseState) {
        extensionManager = context.getModuleServicesManager().getService(ExtensionManager.class);
        extensionManager.registerDeviceType(deviceType);
    }

    @Override
    public void shutdown() {
        BundleUtil.get().removeBundle(ExampleDevice.class);
        extensionManager = context.getModuleServicesManager().getService(ExtensionManager.class);
        extensionManager.unregisterDeviceType(deviceType);
    }
}
