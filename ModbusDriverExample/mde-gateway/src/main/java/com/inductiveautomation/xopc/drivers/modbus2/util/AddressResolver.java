package com.inductiveautomation.xopc.drivers.modbus2.util;

import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;

public interface AddressResolver {

	public ModbusAddress resolve(String address);

}
