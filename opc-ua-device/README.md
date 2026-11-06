# OPC-UA Device Example

### Description
Provides the scaffolding to create a device driver that will register nodes (tags) in Ignition's OPC-UA server.

### Implementation
`ModuleHook` extends `AbstractDeviceModuleHook` and registers the new device type by returning an `ExampleDeviceExtensionPoint` from `getDeviceExtensionPoints`.
`ExampleDeviceExtensionPoint` extends `DeviceExtensionPoint`. It defines the device type's configuration form, validates its settings, and implements `createDevice` to return a new `ExampleDevice`.
That device is created using an `ExampleDeviceConfig`, which in this case only contains one 'custom' field - the number of tags to create per folder.
`ExampleDevice` implements the `Device` interface. Most of the methods should be fairly self explanatory. It extends `ManagedAddressSpaceWithLifecycle` from [Milo](https://github.com/eclipse-milo/milo), the open-source OPC-UA server backing Ignition's UA server, whose `SamplingManager` samples the device's subscribed items, so the device doesn't handle `onDataItemsCreated` and the other data item callbacks itself.