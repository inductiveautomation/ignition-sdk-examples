Report Datasource Example
=========================

This example demonstrates how to add a DataSource to the Ignition Reporting module.  In this example we utilitize some
open source library's to very quickly add datasources in the form of Json responses from REST endpoints on the web.

Usage
-----

1. Clone the SDK Example repo to a local directory.
	```git clone https://github.com/inductiveautomation/ignition-sdk-examples.git```
2. Start up your Developer Gateway (ver 7.8.0+) with the Reporting Module.
3. In the root directory of this example (e.g. ```/Path/ignition-sdk-examples/report-datasource-example/```), build and launch the module on the gateway through the ```mvn install``` command.

That's all it takes to download this example and run it on a developer gateway.  

Information
-----------

The Reporting API provides a simple process to add DataSources.  It requires a few things:
 
1. Create the DataSource by implementing ```ReportDataSource``` in the Gateway Scope.
2. Create a Java serializable data object that can be passed between Gateway and Designer
3. Register the DataSource in the GatewayHook by calling ```GatewayDataSourceRegistry.get().register()``` in the hook's ```startup()``` method.
4. Create a DataSource UI for configuration by extending ```DataSourceConfigPanel```, and implementing a ```AbstractDataSourceConfigFactory``` within it. 
5. Register the factory with the DesignerHook.

The DataSource itself simply provides a way to add to the existing ```Map<String, Object>```.  While there may be some
flexibility in the structure of the Data that the reporting engine can handle, it is recommended that Datasets
(com.inductiveautomation.ignition.common.Dataset) are used.




 