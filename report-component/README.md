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



## Dev Mode Descriptor

The build binds the `ignition-maven-plugin`'s `write-dev-descriptor` goal to the `package` phase, next to the `modl` goal:

```xml
<goals>
    <goal>modl</goal>
    <goal>write-dev-descriptor</goal>
</goals>
```

Running `mvn package` then produces, in the build module's `target/` directory:

* the packaged `*.modl` you install from the Gateway's `Config > Modules` page, and
* a JSON dev descriptor at `target/dev/<moduleId>.json`.

The descriptor records the module metadata (id, name, version, hooks, dependencies) along with each subproject's compiled `target/classes` directory and resolved dependency JARs, letting a development Ignition Gateway load the module straight from the Maven build outputs instead of requiring a full `.modl` install. Because it points at `target/classes`, the module must be compiled first — the `package`-phase binding ensures that. It is generated automatically by the module build, so `mvn package` (re)produces it.
