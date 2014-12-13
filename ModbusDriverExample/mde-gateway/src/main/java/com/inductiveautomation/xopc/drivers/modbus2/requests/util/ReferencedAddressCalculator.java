package com.inductiveautomation.xopc.drivers.modbus2.requests.util;

import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;

/**
 * Calculates the actual register addressed when dealing with a bit on a multi-register datatype.
 * 
 * @author Kevin Herron
 */
public class ReferencedAddressCalculator {

	private final boolean zeroBased;
	private final boolean swapWords;

	public ReferencedAddressCalculator(boolean zeroBased, boolean swapWords) {
		this.zeroBased = zeroBased;
		this.swapWords = swapWords;
	}

	public short calculateReferencedAddress(ModbusAddress address) {
		short referenceAddress = (short) address.getStartAddress();

		if (zeroBased) {
			referenceAddress--;
		}

		int addressSpan = address.getAddressSpan();

		if (!swapWords) {
			referenceAddress += (addressSpan - 1);
			referenceAddress -= (address.getBit() / 16);
		} else {
			referenceAddress += (address.getBit() / 16);
		}

		return referenceAddress;
	}

}
