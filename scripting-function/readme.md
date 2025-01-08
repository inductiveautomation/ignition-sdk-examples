# Scripting Function Example

### Overview

Provides some basic scripting examples, including one which executes through a remote procedure call (RPC).
In client/designer scope, the function delegates to an RPC handler, which then calls a function on the gateway and 
returns the result.

### Implementation

`scripting-function-common` defines the abstract class that all the implementing functions must adhere to, regardless of
scope.

The [`ClientScriptModule`](scripting-function-client/src/main/java/com/inductiveautomation/ignition/examples/scripting/client/ClientScriptModule.java) 
class creates the actual RPC handler, using the API's `GatewayConnection#getRpcInterface` method.
An instance of `ClientScriptModule` is used in the Designer and Client scope to provide details about the function. 
When actually executed, the proxy object returned by `getRpcInterface` will automatically call the corresponding 
interface method on the Gateway, adapting parameters and return types using the provided serializer.

In this way, the proxy handles the "heavy lifting" of passing values back and forth between scopes, and the serialization
and deserialization of values is decoupled from the actual interface methods and implementations.
