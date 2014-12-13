package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial;

import Serialio.SerialConfig;

public enum Handshake {

	None(SerialConfig.HS_NONE),
	XonXoff(SerialConfig.HS_XONXOFF),
	CtsRts(SerialConfig.HS_CTSRTS),
	DsrDtr(SerialConfig.HS_DSRDTR);

	private final int configValue;

	private Handshake(int configValue) {
		this.configValue = configValue;
	}

	public int getConfigValue() {
		return configValue;
	}

	public static Handshake forConfigValue(int configValue) {
		for (Handshake hs : values()) {
			if (hs.getConfigValue() == configValue) {
				return hs;
			}
		}
		return Handshake.None;
	}

}

