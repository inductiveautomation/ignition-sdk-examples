package com.inductiveautomation.ignition.examples.reporting.datasource.common.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.reporting.datasource.common.ExampleReportDatasourceModule;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.ModuleState;

/**
 * GatewayModuleHook is the entry point to the Ignition Gateway.  When the .modl file is built using the Ignition Maven
 * plugin, the hook configured in our build pom will be added to the Ignition Module's module.xml file.  When the
 * module is installed, this xml file is read, and this GatewayHook is loaded into ignition's classpath.
 *
 * @author Perry Arellano-Jones
 */
public class GatewayHook extends AbstractGatewayModuleHook {
    private GatewayContext context;

    private final LoggerEx logger = LoggerEx.newBuilder().build(GatewayHook.class);

    @Override
    public void setup(GatewayContext context) {
        this.context = context;
    }

    @Override
    public void startup(LicenseState licenseState) {
        if (reportingLoaded(context)) {
            ReportingInstaller.setup(context);
        } else {
            var warningMessage = "The Reporting module is not installed or not active. "
                    + "The Example Datasource will not be available. "
                    + "Please install or enable the Reporting module to use this module.";

            logger.warn(warningMessage);
            throw new IllegalStateException(warningMessage);
        }
    }

    private boolean reportingLoaded(GatewayContext context) {
        var reportingModule = context.getModuleManager()
                .getModule(ExampleReportDatasourceModule.REPORTING_MODULE_ID);

        return reportingModule != null
                && (reportingModule.getState() == ModuleState.PENDING
                || reportingModule.getState() == ModuleState.ACTIVE);
    }

    public void shutdown() {

    }
}