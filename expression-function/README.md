# Expression Example
### Description
This module provides an example expression that can be used in a Gateway or Designer/Client.
The actual expression code is located in the `MultiplyFunction` class. 
The `GatewayHook`, `DesignerHook` and `ClientHook` classes all reference this expression class in their `configureFunctionFactory()` methods.

### Implementation
To see the expression in action, start a Designer and add a label to a window.
Click on the label's Text 'Bind Property' button.
In the Property Binding window, select the Expression option and add `exampleMultiply(4, 6)` to the window.
Click OK.
The label will now use the custom expression as its text.