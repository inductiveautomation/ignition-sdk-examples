package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.legacy;

import java.beans.Statement;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.xopc.driver.api.configuration.DeviceSettingsRecord;
import com.inductiveautomation.xopc.driver.util.conversion.DriverPropertyRecord;
import com.inductiveautomation.xopc.driver.util.conversion.LegacyDeviceConfigConverter;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.ModbusRtuOverTcpDriverType;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.ModbusRtuDriverSettings;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.ModbusTcpDriverSettings;

public class LegacyModbusRtuOverTcpConverter extends LegacyDeviceConfigConverter<ModbusTcpDriverSettings> {

	public LegacyModbusRtuOverTcpConverter(GatewayContext context) {
		super(context);
	}

	@Override
	public String getDriverClassName() {
		return "com.inductiveautomation.xopc.drivers.modbus2.ModbusOverTCPDriver";
	}

	@Override
	protected String getDriverTypeId() {
		return ModbusRtuOverTcpDriverType.TYPE_ID;
	}

	@Override
	protected void convertLegacyProperty(DriverPropertyRecord legacyProperty, ModbusTcpDriverSettings driverSettings) {
		Class<?> clazz = legacyProperty.getPropertyType().getPropertyClass();
		String key = legacyProperty.getPropertyKey();
		Object value = TypeUtilities.coerce(legacyProperty.getPropertyValue(), clazz);

		// The setter names in ModbusTcpDriverSettings are predictable: "set" + the value of the legacy property key.

		String setterName = "set" + key;
		try {
			new Statement(driverSettings, setterName, new Object[]{value}).execute();
		} catch (Exception e) {
			getLog().errorf("Error calling setter '%s' with value '%s'.", setterName, value, e);
		}
	}

	@Override
	protected RecordMeta<ModbusTcpDriverSettings> getRecordMeta() {
		return ModbusTcpDriverSettings.META;
	}

	@Override
	protected ReferenceField<DeviceSettingsRecord> getReferenceField() {
		return ModbusTcpDriverSettings.DeviceSettings;
	}

}
