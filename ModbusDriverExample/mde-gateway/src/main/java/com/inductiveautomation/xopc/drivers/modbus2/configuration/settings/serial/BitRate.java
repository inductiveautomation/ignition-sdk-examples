package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial;

import Serialio.SerialConfig;

public enum BitRate {

	BitRate_110(SerialConfig.BR_110),
	BitRate_150(SerialConfig.BR_150),
	BitRate_300(SerialConfig.BR_300),
	BitRate_600(SerialConfig.BR_600),
	BitRate_1200(SerialConfig.BR_1200),
	BitRate_2400(SerialConfig.BR_2400),
	BitRate_4800(SerialConfig.BR_4800),
	BitRate_9600(SerialConfig.BR_9600),
	BitRate_19200(SerialConfig.BR_19200),
	BitRate_38400(SerialConfig.BR_38400),
	BitRate_57600(SerialConfig.BR_57600),
	BitRate_115200(SerialConfig.BR_115200),
	BitRate_230400(SerialConfig.BR_230400),
	BitRate_460800(SerialConfig.BR_460800),
	BitRate_921600(SerialConfig.BR_921600);

	private final int configValue;

	private BitRate(int configValue) {
		this.configValue = configValue;
	}

	public int getConfigValue() {
		return configValue;
	}

	public static BitRate forConfigValue(int configValue) {
		for (BitRate br : values()) {
			if (br.getConfigValue() == configValue) {
				return br;
			}
		}
		return BitRate.BitRate_115200;
	}

}
