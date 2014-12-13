package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial;

import Serialio.SerialConfig;

public enum DataBits {

	DataBits_5(SerialConfig.LN_5BITS),
	DataBits_6(SerialConfig.LN_6BITS),
	DataBits_7(SerialConfig.LN_7BITS),
	DataBits_8(SerialConfig.LN_8BITS);

	private final int configValue;

	private DataBits(int configValue) {
		this.configValue = configValue;
	}

	public int getConfigValue() {
		return configValue;
	}

	public static DataBits forConfigValue(int configValue) {
		for (DataBits dataBits : values()) {
			if (dataBits.getConfigValue() == configValue) {
				return dataBits;
			}
		}
		return DataBits.DataBits_8;
	}
}
