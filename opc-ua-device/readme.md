# OPC-UA Device Example
### Description
Provides the scaffolding to create a device driver that will register nodes (tags) in Ignition's OPC-UA server.

### Implementation
`ModuleHook` registers the new device type with the UA server, though the `ExtensionManager` service.
`ExampleDeviceType` overrides `DeviceType` to provide a new `createDevice` implementation, which will return a new `ExampleDevice`.
That device is created using an `ExampleDeviceSettings` record, which in this case only contains one 'custom' field - the number of tags to create per folder.
`ExampleDevice` implements the `Device` interface. Most of the methods should be fairly self explanatory - a few (`onDataItemsCreated`, etc are delegated to a `SubscriptionModel` that comes from [Milo](https://github.com/eclipse/milo), the open-source OPC-UA server backing Ignition's UA server.)
