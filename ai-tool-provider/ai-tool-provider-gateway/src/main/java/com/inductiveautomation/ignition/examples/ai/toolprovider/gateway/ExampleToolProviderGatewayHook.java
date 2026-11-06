package com.inductiveautomation.ignition.examples.ai.toolprovider.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.ModuleState;

public class ExampleToolProviderGatewayHook extends AbstractGatewayModuleHook {
    public static final String AI_AGENT_MODULE_ID = "com.inductiveautomation.catalyst.agent";

    private final LoggerEx logger = LoggerEx.newBuilder().build(ExampleToolProviderGatewayHook.class);

    private GatewayContext context;

    @Override
    public void setup(GatewayContext context) {
        this.context = context;
        if (!aiAgentLoaded(context)) {
            var warningMessage = """
                The AI Agents module is not installed or not active. \
                The example tools will not be available. \
                Please install or enable the AI Agents module to use this module.""";
            logger.warn(warningMessage);
            throw new IllegalStateException(warningMessage);
        }
        AiAgentInstaller.setup(context);
    }

    private boolean aiAgentLoaded(GatewayContext context) {
        var aiAgentModule = context.getModuleManager().getModule(AI_AGENT_MODULE_ID);

        return aiAgentModule != null
               && (aiAgentModule.getState() == ModuleState.PENDING
                   || aiAgentModule.getState() == ModuleState.ACTIVE);
    }

    @Override
    public void startup(LicenseState activationState) {
        // Tools are registered in setup(); nothing needs to start here.
    }

    @Override
    public void shutdown() {
        AiAgentInstaller.shutdown(context);
    }
}
