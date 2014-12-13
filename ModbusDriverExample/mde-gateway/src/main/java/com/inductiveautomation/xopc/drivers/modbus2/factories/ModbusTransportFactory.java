package com.inductiveautomation.xopc.drivers.modbus2.factories;

import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;

public interface ModbusTransportFactory {

	public ModbusTransport get(byte unitId);

}
