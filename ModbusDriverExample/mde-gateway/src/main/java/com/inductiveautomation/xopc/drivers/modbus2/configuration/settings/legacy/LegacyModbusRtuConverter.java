package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.legacy;

import java.beans.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import Serialio.SerialConfig;
import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.xopc.driver.api.configuration.DeviceSettingsRecord;
import com.inductiveautomation.xopc.driver.util.conversion.DriverPropertyRecord;
import com.inductiveautomation.xopc.driver.util.conversion.LegacyDeviceConfigConverter;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.ModbusRtuDriverType;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.ModbusRtuDriverSettings;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.BitRate;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.DataBits;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.Handshake;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.Parity;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.StopBits;

public class LegacyModbusRtuConverter extends LegacyDeviceConfigConverter<ModbusRtuDriverSettings> {

	public LegacyModbusRtuConverter(GatewayContext context) {
		super(context);
	}

	@Override
	public String getDriverClassName() {
		return "com.inductiveautomation.xopc.drivers.modbus2.serial.ModbusRTUDriver";
	}

	@Override
	protected String getDriverTypeId() {
		return ModbusRtuDriverType.TYPE_ID;
	}

	@Override
	protected void convertLegacyProperty(DriverPropertyRecord legacyProperty, ModbusRtuDriverSettings driverSettings) {
		Class<?> clazz = legacyProperty.getPropertyType().getPropertyClass();
		String key = legacyProperty.getPropertyKey();
		Object value = TypeUtilities.coerce(legacyProperty.getPropertyValue(), clazz);

		// These uglies are a special case... need to convert from the old way of doing a list of properties, which was
		// as a list of strings that the user chose from.
		if (key.equalsIgnoreCase("BitRate")) {
			String valueAsString = TypeUtilities.toString(value);
			value = BitRate.forConfigValue(LEGACY_BIT_RATES.get(valueAsString));
		} else if (key.equalsIgnoreCase("DataBits")) {
			String valueAsString = TypeUtilities.toString(value);
			value = DataBits.forConfigValue(LEGACY_DATA_BITS.get(valueAsString));
		} else if (key.equalsIgnoreCase("Handshake")) {
			String valueAsString = TypeUtilities.toString(value);
			value = Handshake.forConfigValue(LEGACY_HANDSHAKE.get(valueAsString));
		} else if (key.equalsIgnoreCase("Parity")) {
			String valueAsString = TypeUtilities.toString(value);
			value = Parity.forConfigValue(LEGACY_PARITY.get(valueAsString));
		} else if (key.equalsIgnoreCase("StopBits")) {
			String valueAsString = TypeUtilities.toString(value);
			value = StopBits.forConfigValue(LEGACY_STOP_BITS.get(valueAsString));
		}

		// The setter names in ModbusTcpDriverSettings are predictable: "set" + the value of the legacy property key.
		String setterName = "set" + key;
		try {
			new Statement(driverSettings, setterName, new Object[]{value}).execute();
		} catch (Exception e) {
			getLog().errorf("Error calling setter '%s' with value '%s'.", setterName, value, e);
		}
	}

	@Override
	protected RecordMeta<ModbusRtuDriverSettings> getRecordMeta() {
		return ModbusRtuDriverSettings.META;
	}

	@Override
	protected ReferenceField<DeviceSettingsRecord> getReferenceField() {
		return ModbusRtuDriverSettings.DeviceSettings;
	}

	private static final Map<String, Integer> LEGACY_BIT_RATES = new LinkedHashMap<String, Integer>();

	static {
		LEGACY_BIT_RATES.put("110", SerialConfig.BR_110);
		LEGACY_BIT_RATES.put("150", SerialConfig.BR_150);
		LEGACY_BIT_RATES.put("300", SerialConfig.BR_300);
		LEGACY_BIT_RATES.put("600", SerialConfig.BR_600);
		LEGACY_BIT_RATES.put("1200", SerialConfig.BR_1200);
		LEGACY_BIT_RATES.put("2400", SerialConfig.BR_2400);
		LEGACY_BIT_RATES.put("4800", SerialConfig.BR_4800);
		LEGACY_BIT_RATES.put("9600", SerialConfig.BR_9600);
		LEGACY_BIT_RATES.put("19200", SerialConfig.BR_19200);
		LEGACY_BIT_RATES.put("38400", SerialConfig.BR_38400);
		LEGACY_BIT_RATES.put("57600", SerialConfig.BR_57600);
		LEGACY_BIT_RATES.put("115200", SerialConfig.BR_115200);
		LEGACY_BIT_RATES.put("230400", SerialConfig.BR_230400);
		LEGACY_BIT_RATES.put("460800", SerialConfig.BR_460800);
		LEGACY_BIT_RATES.put("921600", SerialConfig.BR_921600);
	}

	private static final Map<String, Integer> LEGACY_DATA_BITS = new LinkedHashMap<String, Integer>();

	static {
		LEGACY_DATA_BITS.put("5", SerialConfig.LN_5BITS);
		LEGACY_DATA_BITS.put("6", SerialConfig.LN_6BITS);
		LEGACY_DATA_BITS.put("7", SerialConfig.LN_7BITS);
		LEGACY_DATA_BITS.put("8", SerialConfig.LN_8BITS);
	}

	private static final Map<String, Integer> LEGACY_HANDSHAKE = new LinkedHashMap<String, Integer>();

	static {
		LEGACY_HANDSHAKE.put("None", SerialConfig.HS_NONE);
		LEGACY_HANDSHAKE.put("XON/XOFF", SerialConfig.HS_XONXOFF);
		LEGACY_HANDSHAKE.put("CTS/RTS", SerialConfig.HS_CTSRTS);
		LEGACY_HANDSHAKE.put("DSR/DTR", SerialConfig.HS_DSRDTR);
	}

	private static final Map<String, Integer> LEGACY_PARITY = new LinkedHashMap<String, Integer>();

	static {
		LEGACY_PARITY.put("Even", SerialConfig.PY_EVEN);
		LEGACY_PARITY.put("Mark", SerialConfig.PY_MARK);
		LEGACY_PARITY.put("None", SerialConfig.PY_NONE);
		LEGACY_PARITY.put("Odd", SerialConfig.PY_ODD);
		LEGACY_PARITY.put("Space", SerialConfig.PY_SPACE);
	}

	private static final Map<String, Integer> LEGACY_STOP_BITS = new LinkedHashMap<String, Integer>();

	static {
		LEGACY_STOP_BITS.put("1", SerialConfig.ST_1BITS);
		LEGACY_STOP_BITS.put("2", SerialConfig.ST_2BITS);
	}

}
