package com.inductiveautomation.examples.reporting.datasource.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.examples.reporting.datasource.common.RestJsonDataSource;
import com.inductiveautomation.reporting.gateway.api.GatewayDataSourceRegistry;

/**
 * GatewayModuleHook is the entry point to the Ignition Gateway.  When the .modl file is built using the Ignition Maven
 * plugin, the hook configured in our build pom will be added to the Ignition Module's module.xml file.  When the
 * module is installed, this xml file is read, and this GatewayHook is loaded into ignition's classpath.
 * @author Perry Arellano-Jones
 */
public class GatewayHook extends AbstractGatewayModuleHook {
    private GatewayContext context;

    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;
    }

    public void startup(LicenseState licenseState) {
        GatewayDataSourceRegistry.get(context).register(new RestJsonDataSource());
    }

    public void shutdown() {

    }
}
