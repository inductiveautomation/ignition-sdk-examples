package com.inductiveautomation.xopc.drivers.modbus2.requests.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusDataType;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;

public class ReferencedAddressCalculatorTest {

	@Test
	public void test_16Bit() {
		ModbusAddress address = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 1)
				.setBit(0)
				.build();

		short referencedAddress = new ReferencedAddressCalculator(false, false).calculateReferencedAddress(address);

		assertEquals(1, referencedAddress);
	}

	@Test
	public void test_32Bit() {
		ModbusAddress bit0 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 1)
				.setBit(0)
				.build();
		ModbusAddress bit16 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 1)
				.setBit(16)
				.build();

		ReferencedAddressCalculator calculator = new ReferencedAddressCalculator(false, false);

		assertEquals(2, calculator.calculateReferencedAddress(bit0));
		assertEquals(1, calculator.calculateReferencedAddress(bit16));
	}

	@Test
	public void test_32Bit_SwapWords() {
		ModbusAddress bit0 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 1)
				.setBit(0)
				.build();
		ModbusAddress bit16 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 1)
				.setBit(16)
				.build();

		ReferencedAddressCalculator calculator = new ReferencedAddressCalculator(false, true);

		assertEquals(1, calculator.calculateReferencedAddress(bit0));
		assertEquals(2, calculator.calculateReferencedAddress(bit16));
	}

	@Test
	public void test_64Bit() {
		ModbusAddress bit0 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int64, 1)
				.setBit(0)
				.build();
		ModbusAddress bit16 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int64, 1)
				.setBit(16)
				.build();
		ModbusAddress bit32 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int64, 1)
				.setBit(32)
				.build();
		ModbusAddress bit48 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int64, 1)
				.setBit(48)
				.build();

		ReferencedAddressCalculator calculator = new ReferencedAddressCalculator(false, false);

		assertEquals(4, calculator.calculateReferencedAddress(bit0));
		assertEquals(3, calculator.calculateReferencedAddress(bit16));
		assertEquals(2, calculator.calculateReferencedAddress(bit32));
		assertEquals(1, calculator.calculateReferencedAddress(bit48));
	}
	
	@Test
	public void test_64Bit_SwapWords() {
		ModbusAddress bit0 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int64, 1)
				.setBit(0)
				.build();
		ModbusAddress bit16 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int64, 1)
				.setBit(16)
				.build();
		ModbusAddress bit32 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int64, 1)
				.setBit(32)
				.build();
		ModbusAddress bit48 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int64, 1)
				.setBit(48)
				.build();

		ReferencedAddressCalculator calculator = new ReferencedAddressCalculator(false, true);

		assertEquals(1, calculator.calculateReferencedAddress(bit0));
		assertEquals(2, calculator.calculateReferencedAddress(bit16));
		assertEquals(3, calculator.calculateReferencedAddress(bit32));
		assertEquals(4, calculator.calculateReferencedAddress(bit48));
	}

}
