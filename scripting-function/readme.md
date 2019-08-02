# Scripting Function Example

### Overview
Provides a basic example of a scripting function which executes through a remote procedure call (RPC).
In a client scope, the function delegates to the module's RPC handler, which then calls `multiply` on the gateway and returns the result.

### Implementation
`scripting-function-common` defines the interface and abstract class that all the implementing functions must adhere to, regardless of scope.

The [`ClientScriptModule`](scripting-function-client/src/main/java/com/inductiveautomation/ignition/examples/scripting/client/ClientScriptModule.java) 
class creates the actual RPC handler, using the API's `ModuleRPCFactory.create()` method.
An instance of `ClientScriptModule` is used in the Designer and Client scope to provide details about the function. 
When actually executed, the `ModuleRPCFactory` will automatically call the `GatewayHook`'s `getRPCHandler` method, which returns the [`GatewayScriptModule`](scripting-function-gateway/src/main/java/com/inductiveautomation/ignition/examples/scripting/GatewayScriptModule.java) with the actual implementation of `MathBlackBox`.

The `ModuleRPCFactory` handles the "heavy lifting" of passing values back and forth between scopes.


