package com.inductiveautomation.ignition.examples.ai.toolprovider.gateway;

import com.inductiveautomation.ai.agent.common.mcp.protocol.ToolHandler.CallToolResult;
import com.inductiveautomation.ai.agent.common.primitive.PrimitiveProvider;
import com.inductiveautomation.ai.agent.common.primitive.PrimitiveTool;
import com.inductiveautomation.ai.agent.common.primitive.Primitives;
import com.inductiveautomation.ignition.common.ai.ToolParam;
import com.inductiveautomation.ignition.common.gson.JsonArray;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.modules.ModuleInfo;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

/// Provides an example tool to AI Agents. `list_modules` is an annotated method: [Primitives#fromAnnotated(Object...)]
/// reads the [PrimitiveTool] and [ToolParam] annotations and generates the tool's input schema.
public class ExampleToolProvider implements PrimitiveProvider {

    private final GatewayContext context;
    private final List<ProvidedTool> tools;

    public ExampleToolProvider(GatewayContext context) {
        this.context = context;

        var tools = new ArrayList<ProvidedTool>();
        tools.addAll(Primitives.fromAnnotated(this));
        this.tools = List.copyOf(tools);
    }

    @Override
    public List<ProvidedTool> getTools() {
        return tools;
    }

    /// Annotated tool.
    /// - The method must return [CallToolResult]; Note: a method with any other return type is skipped
    /// without a warning, so the tool simply does not appear.
    /// - The method is private on purpose. Nothing calls it directly: [Primitives#fromAnnotated(Object...)]
    /// finds it by reflection and makes it accessible. Your IDE may flag it as unused; don't delete it
    /// or make it public.
    /// - Set [ToolParam#name()] explicitly, since parameter names are not available through reflection
    /// unless the code is compiled with the `-parameters` flag.
    @PrimitiveTool(
        name = "list_modules",
        title = "List Gateway Modules",
        description = """
            Lists the modules installed on the Ignition Gateway, with each module's id, name, version, \
            and state.""")
    private CallToolResult listModules(
        @Nullable
        @ToolParam(
            name = "nameFilter",
            description = "Optional case-insensitive text. Only modules whose name contains it are returned.",
            required = false)
        String nameFilter) {
        var modules = new JsonArray();
        for (var module : context.getModuleManager().getModules()) {
            ModuleInfo info = module.getInfo();
            if (nameFilter != null
                && !info.getName().toLowerCase(Locale.ROOT).contains(nameFilter.toLowerCase(Locale.ROOT))) {
                continue;
            }

            var json = new JsonObject();
            json.addProperty("id", info.getId());
            json.addProperty("name", info.getName());
            json.addProperty("version", info.getVersion().toString());
            json.addProperty("state", module.getState().name());
            modules.add(json);
        }

        var result = new JsonObject();
        result.add("modules", modules);
        return new CallToolResult(result);
    }
}
