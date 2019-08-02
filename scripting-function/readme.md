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


# Adding to the Example
The initial example demonstrates the steps involved with adding a new system function into Ignition with the module SDK, but let's take the example a bit further by adding a new function.

This section assumes you are able to successfully build a module from the initial example already. If you're having any trouble, head back to the [Getting Started section](https://github.com/inductiveautomation/ignition-sdk-examples#getting-started) at the root of these examples. 

## Adding a Function
In the initial example, the multiply() function is defined under the AbstractScriptModule class (located under `scripting-function-common`). Let's add a simple function:

```Java    
public String helloWorld(){
        return "Hi there!";
}
```

That's it! We're ready to test it out! Build the module, install it, launcher the designer, open the Script Console and test it out

```python
print system.example.helloWorld()
```

### Adding Descriptions
If you examine at the contents of the autocomplete descriptions (Ctrl+Space bar) you'll notice that our function does not have any descriptions. Obviously we haven't added any yet, so let's look at how to do that.

The Programmer's Guide contains the full details on all of the [Module SDK's Annotations](https://docs.inductiveautomation.com/display/SE/Adding+Scripting+Functions#AddingScriptingFunctions-FunctionAnnotations). In short, we can use the `@ScriptFunction` annotation to denote where the descriptions for our function should be. We can add it to our hellowWorld()


```Java 
@ScriptFunction(docBundlePrefix = "AbstractScriptModule")
public String helloWorld(){
    return "Hi there!";
}
```

In this case, the "docBundlePrefix" argument is stating that the `AbstractScriptModule` properties file (also located under `scripting-functions-common`) has the descriptions that should be used in conjunction with this function. 

Next we need to actually write the descriptions. Open the `AbstractScriptModule.properties` file. You'll see the descriptions for the multiply() function. In short:

- `desc` is the description of the function
- `param.%argName%` is the description for a particular argument
- `returns` is the description for the return value

Our function doesn't accept any arguments, so we only have two entries to add:

```
helloWorld.desc=Returns a friendly greeting
helloWorld.returns=The string "Hi There!"
```
Build the module, install the new module on top of the old, and relaunch the designer, and check the autocomplete popup. You should see your new descriptions. 
