# Report Datasource Example
This example demonstrates how to add a `DataSource` to the Ignition Reporting module. We will create a new DataSource
that can be used in the Reporting module to retrieve data from REST endpoints.

### Information
The Reporting API provides a simple process to add DataSources.  It requires a few things:

1. Create the DataSource by implementing `ReportDataSource` in the Gateway Scope.
2. Create a `DataSourceConfigObject` that can be passed between the Gateway and Designer. The object is serialized
   for RPC by overriding the `toJson()` method. It should be coupled with a static `fromJson()` methods to handle the
   deserialization of the object.
3. Register the DataSource in the GatewayHook by calling `GatewayDataSourceRegistry.get().register()` in the hook's `startup()` method.
4. Create a DataSource UI for configuration by extending `DataSourceConfigPanel`, and implementing `AbstractDataSourceConfigFactory` within it.
   This config panel is used to view and edit the `DataSourceConfigObject` created in step 2.
5. Register the factory with the DesignerHook.

The `DataSource` itself simply provides a way to add to the existing `Map<String, Object>`.  While there may be some
flexibility in the structure of the Data that the reporting engine can handle, it is recommended that Datasets
(`com.inductiveautomation.ignition.common.Dataset`) are used for compatibility.

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
