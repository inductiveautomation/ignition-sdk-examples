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