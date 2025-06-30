package com.inductiveautomation.ignition.examples.tagdriver;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.examples.tagdriver.settings.ExampleDeviceSettings;
import com.inductiveautomation.ignition.gateway.config.migration.ExtensionPointRecordMigrationStrategy;
import com.inductiveautomation.ignition.gateway.config.migration.IdbMigrationStrategy;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.opcua.server.api.AbstractDeviceModuleHook;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceExtensionPoint;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceSettingsRecord;
import java.util.List;

public class ModuleHook extends AbstractDeviceModuleHook {

  @Override
  public void setup(GatewayContext context) {
    super.setup(context);

    BundleUtil.get().addBundle(ExampleDevice.class);
  }

  @Override
  public void startup(LicenseState activationState) {
    super.startup(activationState);
  }

  @Override
  public void shutdown() {
    super.shutdown();

    BundleUtil.get().removeBundle(ExampleDevice.class);
  }

  @Override
  protected List<DeviceExtensionPoint<?>> getDeviceExtensionPoints() {
    return List.of(new ExampleDeviceExtensionPoint());
  }

  @Override
  public List<IdbMigrationStrategy> getRecordMigrationStrategies() {
    @SuppressWarnings("deprecation")
    var strategy =
        ExtensionPointRecordMigrationStrategy.newBuilder(ExampleDeviceExtensionPoint.TYPE_ID)
            .resourceType(DeviceExtensionPoint.DEVICE_RESOURCE_TYPE)
            .profileMeta(DeviceSettingsRecord.META)
            .settingsRecordForeignKey(ExampleDeviceSettings.DEVICE_SETTINGS)
            .settingsMeta(ExampleDeviceSettings.META)
            .settingsEncoder(
                b -> b.withCustomFieldName(ExampleDeviceSettings.TAG_COUNT, "general.tagCount"))
            .build();

    return List.of(strategy);
  }
}
