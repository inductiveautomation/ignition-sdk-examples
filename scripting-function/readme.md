# Scripting Example

Provides a basic example of a scripting function which executes through a remote procedure call (RPC).
In a client scope, the function delegates to the modules RPC handler, which then calls `multiply` on the gateway side and returns the result.
