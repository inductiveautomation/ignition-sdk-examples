package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial;

import Serialio.SerialConfig;

public enum StopBits {

	StopBits_1(SerialConfig.ST_1BITS),
	StopBits_2(SerialConfig.ST_2BITS);

	private final int configValue;

	private StopBits(int configValue) {
		this.configValue = configValue;
	}

	public int getConfigValue() {
		return configValue;
	}

	public static StopBits forConfigValue(int configValue) {
		for (StopBits sb : values()) {
			if (sb.getConfigValue() == configValue) {
				return sb;
			}
		}
		return StopBits.StopBits_1;
	}

}
