# AI Tool Provider Example
This example shows how a module can add its own tools to the Catalyst Agent module. An Agent can call these tools 
during a conversation, and they are also served to MCP clients through the Gateway's hosted MCP servers.

The module registers one tool provider, with the id `com.inductiveautomation.ignition.examples.ai-tool-provider/example`.
The provider offers two tools, and each one is built in a different way:

| Tool                  | How it is built                                                                                                                                                    | What it does                                                                     |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| `list_modules`        | An annotated method. `Primitives.fromAnnotated(...)` reads the `@PrimitiveTool` and `@ToolParam` annotations and generates the input schema.                       | Lists the modules installed on the Gateway, optionally filtered by name.         |
| `convert_temperature` | A hand-built `ProvidedTool`, made from a `ToolDescription`, a JSON input schema, and a handler. Use this approach when you need full control, such as an `enum`. | Converts a temperature between Celsius, Fahrenheit, and Kelvin.                  |

## How it works
1. `ExampleToolProviderGatewayHook` checks that the Catalyst Agent module (`com.inductiveautomation.catalyst.agent`) is
   installed and running. If it is not, the hook logs a warning and fails setup.
2. During setup, `AiAgentInstaller` gets the Catalyst Agent module's `PrimitiveProviderRegistry` and registers an
   `ExampleToolProvider` under the module's `PrimitiveId`. On shutdown, it unregisters it.
3. `AiAgentInstaller` is the only class that the hook calls directly to reach the Catalyst Agent classes. This keeps the 
   hook from failing with a `NoClassDefFoundError` when the Catalyst Agent module is not installed.

The Catalyst Agent module's id is `com.inductiveautomation.catalyst.agent`, but its Java packages are still
`com.inductiveautomation.ai.agent.*`. The module id is what the build's `<depends>` entry and the hook's module lookup
use; the package name is only what the imports use.

## Requirements
This example requires the Catalyst Agent module, which is available in Ignition 2027.2.0 and later. The example is
compiled for Java 25.

## Seeing it in action
Registering a tool provider does not make its tools available on its own. An Agent or a hosted MCP server only offers
the tools it has been configured to use. The steps below install the module, check that the tools were registered,
and then call them two ways: from an Agent, and from an MCP client.

The URLs below use the Gateway's default HTTP port, `8088`. If your Gateway uses a different port (for example, a
Docker container that maps `8088` to another host port), use that port instead.

### 1. Build and install the module
1. Run `mvn package` from this directory. The module file is written to
   `ai-tool-provider-build/target/AI-Tool-Provider-Example-unsigned.modl`.
2. The module is unsigned. To install it, the Gateway must be started with the `ignition.allowunsignedmodules` system
   property set to `true`, e.g. by adding `wrapper.java.additional.<n>=-Dignition.allowunsignedmodules=true` to
   `ignition.conf`.
3. Make sure the Catalyst Core and Catalyst Agent modules are installed and enabled (**Platform > System > Modules**),
   then install the `.modl` file on the Gateway. If you enable Catalyst Agent after installing this module, restart
   this module too: it checks for Catalyst Agent only once, when it starts.

### 2. Check that the tools are registered
The Catalyst Agent module lists every registered tool provider at `/data/catalyst-agent/primitives`. While you are
logged in to the Gateway, open:

```
http://localhost:8088/data/catalyst-agent/primitives
```

The response has one entry per provider. Find the one with `"moduleId": "com.inductiveautomation.ignition.examples.ai-tool-provider"`
and `"typeId": "example"`. Its `tools` list should contain `list_modules` and `convert_temperature`, with the input
schema of each one. If the entry is missing, check the Gateway logs for the warning from
`ExampleToolProviderGatewayHook`, which means the Catalyst Agent module was not installed or was not running.

If the page returns `HTTP ERROR 404 Not Found`, the Catalyst Agent module itself is not running, because this URL
only exists while it is. Enable it under **Platform > System > Modules**, then restart this module.

### 3. Call the tools from an Agent
An Agent needs a model to talk to, so this path needs an account with an AI model provider.

1. Go to **Connections > Catalyst > Model Providers** and create a model provider.
2. Go to **Services > Catalyst > Agents** and create an Agent named `ToolDemo` that uses that model provider. The name
   must match the `agent` argument in the script below.
   1. The **System Prompt** is required. Use one that tells the model to answer with its tools, for example:

      ```
      You are a helper for an Ignition Gateway. Answer questions by calling your tools rather than from memory.
      Use list_modules for any question about which modules are installed; pass a name filter when the question names one.
      Use convert_temperature for any temperature conversion; call it once per target unit.
      Keep answers short and state the values the tools returned.
      ```

      Without such a prompt, a model can answer the temperature question from memory and never call
      `convert_temperature`, so the demo would not show the tool working.
   2. The default **Permissions** (`Authenticated`) are fine for calling the Agent from the Designer's Script Console.
      Autonomous callers, such as timer scripts and tag events, hold no security levels, so they can only invoke the
      Agent when no permissions are selected.
   3. The create form does not include tools. After creating the Agent, open its menu (the **⋮** button), choose
      **Configure Tools**, and enable the `com.inductiveautomation.ignition.examples.ai-tool-provider/example` tools. If you skip this, the Agent still
      replies, but it cannot call either tool.
3. Open the Script Console in the Designer and send the Agent a prompt that it can only answer by using the tools:

   ```python
   print system.ai.sendMessage(agent="ToolDemo", prompt="Which Catalyst modules are installed on this Gateway?")
   print system.ai.sendMessage(agent="ToolDemo", prompt="What is 98.6 Fahrenheit in Celsius and in Kelvin?")
   ```

   The first prompt should cause the Agent to call `list_modules` with a name filter, such as `Catalyst`. The second
   should cause it to call `convert_temperature`, giving 37 °C and 310.15 K.

### 4. Call the tools from an MCP client
A hosted MCP server serves tools to MCP clients outside of Ignition. This path does not need a model provider, because
the MCP client calls the tools directly.

1. Go to **Services > Catalyst > MCP Servers** and create an MCP server named `example`. In its tools, enable the
   `com.inductiveautomation.ignition.examples.ai-tool-provider/example` tools.
2. Create an API key on the Gateway, with a security level that the MCP server's permissions allow (by default,
   `Authenticated`). MCP clients authenticate by sending the key in the `X-Ignition-API-Token` header. If you connect
   over plain HTTP, clear the key's **Require secure connections for API Keys** option; otherwise the key is only
   accepted over HTTPS.
3. Connect an MCP client to the server using the Streamable HTTP transport. For example, with the
   [MCP Inspector](https://github.com/modelcontextprotocol/inspector):
   1. Run `npx @modelcontextprotocol/inspector`.
   2. Set the URL to `http://localhost:8088/data/catalyst-agent/mcp-servers/example`.
   3. Add the header `X-Ignition-API-Token` with your API key as its value.
4. List the server's tools. `list_modules` and `convert_temperature` should be listed. Call `convert_temperature`
   with `value` = `100`, `from` = `C`, and `to` = `F`. The result should be `{"value":212.0,"unit":"F"}`.
