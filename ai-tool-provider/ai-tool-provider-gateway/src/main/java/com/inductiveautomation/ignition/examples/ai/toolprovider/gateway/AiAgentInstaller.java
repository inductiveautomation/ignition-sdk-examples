package com.inductiveautomation.ignition.examples.ai.toolprovider.gateway;

import com.inductiveautomation.ai.agent.common.primitive.PrimitiveId;
import com.inductiveautomation.ai.agent.gateway.AiAgentGatewayHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/// Holds the code that uses Catalyst Agent types. The hook only calls into this class after
/// `ExampleToolProviderGatewayHook.aiAgentLoaded` has confirmed the Catalyst Agent module is present, so these
/// types always resolve. Referencing them directly from the hook would fail with [NoClassDefFoundError] on a
/// Gateway without Catalyst Agent, because the module's `<depends>` entry is a soft dependency.
public class AiAgentInstaller {

    /// Identifies this module's tools to the AI Agents module. Agents enable tools by this id, which is shown as
    /// `"<moduleId>/<typeId>"`, e.g. `"com.inductiveautomation.ignition.examples.ai-tool-provider/example"`.
    public static final PrimitiveId PRIMITIVE_ID =
        new PrimitiveId("com.inductiveautomation.ignition.examples.ai-tool-provider", "example");

    public static void setup(GatewayContext context) {
        AiAgentGatewayHook.get(context).ifPresent(hook ->
            hook.getPrimitiveProviderRegistry().register(PRIMITIVE_ID, new ExampleToolProvider(context))
        );
    }

    public static void shutdown(GatewayContext context) {
        AiAgentGatewayHook.get(context).ifPresent(hook ->
            hook.getPrimitiveProviderRegistry().unregister(PRIMITIVE_ID)
        );
    }

}
