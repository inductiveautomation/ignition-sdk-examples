# Scripting Function Example

Provides a basic example of a scripting function which executes through a remote procedure call (RPC).
In a client scope, the function delegates to the module's RPC handler, which then calls `multiply` on the gateway and returns the result.
