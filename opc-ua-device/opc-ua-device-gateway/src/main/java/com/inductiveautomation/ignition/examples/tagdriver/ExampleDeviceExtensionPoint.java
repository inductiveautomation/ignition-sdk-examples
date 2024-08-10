package com.inductiveautomation.ignition.examples.tagdriver;

import com.inductiveautomation.ignition.gateway.opcua.server.api.Device;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceExtensionPoint;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceProfileConfig;

public class ExampleDeviceExtensionPoint extends DeviceExtensionPoint<ExampleDeviceConfig> {

  public static final String TYPE_ID = "UADriverExample";

  public ExampleDeviceExtensionPoint() {
    super(
        TYPE_ID,
        "ExampleDevice.Meta.DisplayName",
        "ExampleDevice.Meta.Description",
        ExampleDeviceConfig.class
    );
  }

  @Override
  protected Device createDevice(
      DeviceContext context,
      DeviceProfileConfig profileConfig,
      ExampleDeviceConfig deviceConfig
  ) {

    return new ExampleDevice(context, deviceConfig);
  }

}
