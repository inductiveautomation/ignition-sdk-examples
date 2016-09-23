package com.inductiveautomation.ignition.examples.designer;

import com.inductiveautomation.ignition.examples.client.GetLogsClientFunctions;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.examples.GetLogsScriptFunctions.Documentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main designer entry point to the remote logging module. We reuse the GetLogsClientFunctions from the client
 * jar, so we do not have to maintain a separate copy here.
 */
public class DesignerHook extends AbstractDesignerModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        manager.addScriptModule("system.example", new GetLogsClientFunctions(), new Documentation());
    }

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

}
