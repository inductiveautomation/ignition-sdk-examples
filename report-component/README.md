Reporting Component Example
===========================

This is a small example intended to demonstrate the basics of adding a new Component to the Design Palette of the 
Ignition Reporting Module by using the Ignition Reporting Module SDK.  By adding a component to the palette you enable
the design and generation of custom objects on Reports.  These can be simple shape objects such as this example, or
far more complex objects such as shapes or custom tables.

  
Usage
-----

1. Clone the SDK Example repo to a local directory.
	```git clone https://github.com/inductiveautomation/ignition-sdk-examples.git```
2. Start up your Developer Gateway (ver 7.8.0+) with the Reporting Module installed.
3. In the root directory of this example (e.g. ```<YourPath>/ignition-sdk-examples/report-component/```), 
build and launch the module on the gateway through the ```mvn install``` command.

That's all it takes to download this example and run it on a developer gateway.  

Information
-----------

The Reporting API provides a simple process to add Shapes, but there are a few things to be aware of.  
 
1. All shapes must extend from ```RMShape```
2. To allow access to the Shape from all contexts, create your Shape in a Common jar shared across scopes.
3. You need to register shapes:
    a. The Shape needs to be added to ```RMArchiver.registerClass("a-unique-archive-name", ShapeClass.class)``` in your
     Gateway and Designer hook classes' startup().  This step registers the shape with the report engine serialization 
     system.  If you are finding that your component works in the designer, but not in the preview, this step may be missing!
    b. To add the Component/Shape to the Palette, call ```DesignerShapeRegistry.get(context).register(ShapeClass.class)```
    in your Designer hook startup.


