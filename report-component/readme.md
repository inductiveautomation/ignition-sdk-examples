# Reporting Component Example

This is a small example intended to demonstrate the basics of adding a new Component to the Design Palette of the 
Ignition Reporting Module by using the Ignition Reporting Module SDK.  By adding a component to the palette you enable
the design and generation of custom objects on Reports.  These can be simple shape objects such as this example, or
far more complex objects such as shapes or custom tables.

### Information

The Reporting API provides a simple process to add Shapes, but there are a few things to be aware of.  
 
1. All shapes must extend from `RMShape`
2. To allow access to the Shape from all contexts, create your Shape in a Common jar shared across scopes.
3. You need to register shapes:
    * The Shape needs to be added to `RMArchiver.registerClass("a-unique-archive-name", ShapeClass.class)` in your
     Gateway and Designer hook classes' `startup()`.  This step registers the shape with the report engine serialization 
     system.  If you find that your component works in the designer, but not in the preview, this step may be missing!
    * To add the Component/Shape to the Palette, call `DesignerShapeRegistry.get(context).register(ShapeClass.class)`
    in your Designer hook startup.


