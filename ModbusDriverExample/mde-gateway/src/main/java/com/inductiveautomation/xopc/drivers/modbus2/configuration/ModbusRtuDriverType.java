package com.inductiveautomation.xopc.drivers.modbus2.configuration;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.web.components.ConfigPanel;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;
import com.inductiveautomation.xopc.driver.api.Driver;
import com.inductiveautomation.xopc.driver.api.DriverContext;
import com.inductiveautomation.xopc.driver.api.configuration.DeviceSettingsRecord;
import com.inductiveautomation.xopc.driver.api.configuration.DriverType;
import com.inductiveautomation.xopc.driver.api.configuration.links.ConfigurationUILink;
import com.inductiveautomation.xopc.driver.api.configuration.links.LinkEntry;
import com.inductiveautomation.xopc.driver.util.diagnostics.DiagnosticsLink;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.ModbusRtuDriverSettings;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.web.ModbusConfigurationUI;
import com.inductiveautomation.xopc.drivers.modbus2.serial.ModbusRTUDriver;

public class ModbusRtuDriverType extends DriverType {

	public static final String TYPE_ID = "ModbusRtu";

	public ModbusRtuDriverType() {
		super(TYPE_ID, "Modbus." + "ModbusRtuDriverType.Name", "Modbus." + "ModbusRtuDriverType.Description");
	}

	@Override
	public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
		return ModbusRtuDriverSettings.META;
	}

	@Override
	public ReferenceField<?> getSettingsRecordForeignKey() {
		return ModbusRtuDriverSettings.DeviceSettings;
	}

	@Override
	public List<LinkEntry> getLinks() {
		return Lists.newArrayList(new AddressConfigLink(), new DiagnosticsLink());
	}

	@Override
	public Driver createDriver(DriverContext driverContext, DeviceSettingsRecord deviceSettings) {
		ModbusRtuDriverSettings settings = findProfileSettingsRecord(driverContext.getGatewayContext(), deviceSettings);

		return new ModbusRTUDriver(driverContext, settings);
	}

	private static class AddressConfigLink implements ConfigurationUILink {

		@Override
		public String getLinkText(Locale locale) {
			return BundleUtil.get().getStringLenient(locale, "words.addresses");
		}

		@Override
		public ConfigPanel getConfigurationUI(
				IConfigPage configPage,
				ConfigPanel returnPanel,
				PersistentRecord record,
				Callback callback) {

			return new ModbusConfigurationUI(configPage, returnPanel, callback, record);
		}

	}

}
