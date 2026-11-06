package com.inductiveautomation.ignition.examples.ai.toolprovider.gateway;

import com.inductiveautomation.ai.agent.common.mcp.protocol.ToolHandler.CallToolResult;
import com.inductiveautomation.ai.agent.common.primitive.PrimitiveProvider;
import com.inductiveautomation.ai.agent.common.primitive.PrimitiveTool;
import com.inductiveautomation.ai.agent.common.primitive.Primitives;
import com.inductiveautomation.ai.agent.common.tool.ToolDescription;
import com.inductiveautomation.ignition.common.ai.ToolParam;
import com.inductiveautomation.ignition.common.auth.permissions.PermissionsConfig;
import com.inductiveautomation.ignition.common.gson.JsonArray;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.gson.JsonParser;
import com.inductiveautomation.ignition.common.modules.ModuleInfo;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

/// Provides two example tools to AI Agents, each built a different way:
///
/// - `list_modules` is an annotated method. [Primitives#fromAnnotated(Object...)] reads the [PrimitiveTool] and
///   [ToolParam] annotations and generates the tool's input schema.
/// - `convert_temperature` is built by hand from a [ToolDescription], a JSON input schema, and a handler. Use this
///   approach when you need full control over the schema, such as an `enum`.
public class ExampleToolProvider implements PrimitiveProvider {

    private final GatewayContext context;
    private final List<ProvidedTool> tools;

    public ExampleToolProvider(GatewayContext context) {
        this.context = context;

        var tools = new ArrayList<ProvidedTool>();
        tools.addAll(Primitives.fromAnnotated(this));
        tools.add(createConvertTemperatureTool());
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

    /// Number of decimal places `convert_temperature` rounds to when the AI client omits the optional `decimals`
    /// argument. Keep it in sync with the `default` in [#CONVERT_TEMPERATURE_SCHEMA].
    private static final int DEFAULT_DECIMALS = 2;

    /// Hand-built tool. The input schema is a JSON Schema object, the same format MCP uses for a tool's
    /// `inputSchema`. A syntax error here throws when the class loads, so it surfaces at module startup.
    private static final JsonObject CONVERT_TEMPERATURE_SCHEMA = JsonParser.parseString("""
        {
          "type": "object",
          "properties": {
            "value": {
              "type": "number",
              "description": "The temperature to convert."
            },
            "from": {
              "type": "string",
              "description": "The unit to convert from: C (Celsius), F (Fahrenheit), or K (Kelvin).",
              "enum": ["C", "F", "K"]
            },
            "to": {
              "type": "string",
              "description": "The unit to convert to: C (Celsius), F (Fahrenheit), or K (Kelvin).",
              "enum": ["C", "F", "K"]
            },
            "decimals": {
              "type": "integer",
              "description": "Optional. Number of decimal places to round the result to. Defaults to 2.",
              "minimum": 0,
              "maximum": 10,
              "default": 2
            }
          },
          "required": ["value", "from", "to"]
        }
        """).getAsJsonObject();

    private ProvidedTool createConvertTemperatureTool() {
        var description = new ToolDescription(
            "convert_temperature",
            "Convert Temperature",
            "Converts a temperature between Celsius, Fahrenheit, and Kelvin.",
            CONVERT_TEMPERATURE_SCHEMA
        );

        return new ProvidedTool(
            "convert_temperature",
            description,
            args -> {
                // The input schema only tells the AI client what to send; nothing checks the arguments against it
                // before this handler runs. Any exception thrown here becomes an error result the client can read.
                double celsius = toCelsius(args.get("value").getAsDouble(), args.get("from").getAsString());
                double converted = fromCelsius(celsius, args.get("to").getAsString());

                // "decimals" is optional. The schema's "default" is only a hint to the AI client, so apply it here.
                int decimals = args.has("decimals") && !args.get("decimals").isJsonNull()
                    ? args.get("decimals").getAsInt()
                    : DEFAULT_DECIMALS;
                // The schema's "minimum" and "maximum" are hints too. setScale accepts any int: a negative one rounds
                // to tens or hundreds, and a huge one can use up the Gateway's memory.
                if (decimals < 0 || decimals > 10) {
                    throw new IllegalArgumentException("'decimals' must be from 0 to 10.");
                }
                double rounded = BigDecimal.valueOf(converted)
                    .setScale(decimals, RoundingMode.HALF_UP)
                    .doubleValue();

                var result = new JsonObject();
                result.addProperty("value", rounded);
                result.addProperty("unit", args.get("to").getAsString());
                return new CallToolResult(result);
            },
            PermissionsConfig.of(PermissionsConfig.Type.AllOf)
        );
    }

    private static double toCelsius(double value, String unit) {
        return switch (unit) {
            case "C" -> value;
            case "F" -> (value - 32) * 5 / 9;
            case "K" -> value - 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + unit);
        };
    }

    private static double fromCelsius(double celsius, String unit) {
        return switch (unit) {
            case "C" -> celsius;
            case "F" -> celsius * 9 / 5 + 32;
            case "K" -> celsius + 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + unit);
        };
    }
}
