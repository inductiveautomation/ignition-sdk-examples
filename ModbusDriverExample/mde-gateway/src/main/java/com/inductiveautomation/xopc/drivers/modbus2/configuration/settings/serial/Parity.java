package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial;

import Serialio.SerialConfig;

public enum Parity {

	Even(SerialConfig.PY_EVEN),
	Mark(SerialConfig.PY_MARK),
	None(SerialConfig.PY_NONE),
	Odd(SerialConfig.PY_ODD),
	Space(SerialConfig.PY_SPACE);

	private final int configValue;

	private Parity(int configValue) {
		this.configValue = configValue;
	}

	public int getConfigValue() {
		return configValue;
	}

	public static Parity forConfigValue(int configValue) {
		for (Parity p : values()) {
			if (p.getConfigValue() == configValue) {
				return p;
			}
		}
		return Parity.None;
	}
}
