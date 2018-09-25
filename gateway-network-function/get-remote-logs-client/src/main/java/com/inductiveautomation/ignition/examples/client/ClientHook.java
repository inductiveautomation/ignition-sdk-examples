package com.inductiveautomation.ignition.examples.client;

import com.inductiveautomation.ignition.examples.GetLogsScriptFunctions;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main client entry point to the remote logging module.
 */
public class ClientHook extends AbstractClientModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        manager.addScriptModule("system.example", new GetLogsClientFunctions(), new GetLogsScriptFunctions.Documentation());
    }

    @Override
    public void startup(ClientContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

}
