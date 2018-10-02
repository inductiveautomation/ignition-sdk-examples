package com.inductiveautomation.ignition.examples.report.component.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.examples.report.component.shapes.Smiley;
import com.inductiveautomation.rm.archiver.RMArchiver;

/**
 * @author Perry Arellano-Jones
 */
public class GatewayHook extends AbstractGatewayModuleHook {
    private GatewayContext context;

    @Override
    public void setup(GatewayContext context) {
        this.context = context;
    }

    @Override
    public void startup(LicenseState activationState) {
        // shape classes need to be registered from the gateway and designer hooks
        RMArchiver.registerClass(Smiley.ARCHIVE_NAME, Smiley.class);
    }

    @Override
    public void shutdown() {

    }
}
