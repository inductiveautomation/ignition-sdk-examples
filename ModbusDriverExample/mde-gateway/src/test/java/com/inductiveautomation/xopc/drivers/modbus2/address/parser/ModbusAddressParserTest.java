/*******************************************************************************
 * INDUCTIVE AUTOMATION PUBLIC LICENSE 
 * 
 * BY DOWNLOADING, INSTALLING AND/OR IMPLEMENTING THIS SOFTWARE YOU AGREE 
 * TO THE FOLLOWING LICENSE: 
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are 
 * met: 
 * 
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions in 
 * binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. Neither the name of 
 * Inductive Automation nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS 
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INDUCTIVE 
 * AUTOMATION BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * LICENSEE SHALL INDEMNIFY, DEFEND AND HOLD HARMLESS INDUCTIVE AUTOMATION, 
 * ITS SHAREHOLDERS, OFFICERS, DIRECTORS, EMPLOYEES, AGENTS, ATTORNEYS, 
 * SUCCESSORS AND ASSIGNS FROM ANY AND ALL claims, debts, liabilities, 
 * demands, suits and causes of action, known or unknown, in any way 
 * relating to the LICENSEE'S USE OF THE SOFTWARE IN ANY FORM OR MANNER
 * WHATSOEVER AND FOR any act or omission related thereto.
 ******************************************************************************/
package com.inductiveautomation.xopc.drivers.modbus2.address.parser;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.EnumSet;

import org.junit.Test;

import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.AddressType;

public class ModbusAddressParserTest {

	@Test
	public void testHoldingRegister_AllAddressTypes() {
		EnumSet<AddressType> addressTypes = EnumSet.of(
				AddressType.HoldingRegister,
				AddressType.HoldingRegisterBcd,
				AddressType.HoldingRegisterBcd32,
				AddressType.HoldingRegisterFloat,
				AddressType.HoldingRegisterInt32,
				AddressType.HoldingRegisterString,
				AddressType.HoldingRegisterUInt16,
				AddressType.HoldingRegisterUInt32);

		for (AddressType addressType : addressTypes) {
			String addressString = String.format("%s2000", addressType.getShortString());
			ModbusAddress address = ModbusAddress.parse(addressString);

			assertNotNull(String.format("Expected %s to parse.", addressString), address);
			assertEquals(addressType.getModbusTable(), address.getTable());
			assertEquals(addressType.getModbusDataType(), address.getModbusDataType());
		}
	}

	@Test
	public void testInputRegister_AllAddressTypes() {
		EnumSet<AddressType> addressTypes = EnumSet.of(
				AddressType.InputRegister,
				AddressType.InputRegisterBcd,
				AddressType.InputRegisterBcd32,
				AddressType.InputRegisterFloat,
				AddressType.InputRegisterInt32,
				AddressType.InputRegisterUInt16,
				AddressType.InputRegisterUInt32);

		for (AddressType addressType : addressTypes) {
			String addressString = String.format("%s2000", addressType.getShortString());
			ModbusAddress address = ModbusAddress.parse(addressString);

			assertNotNull(String.format("Expected %s to parse.", addressString), address);
			assertEquals(addressType.getModbusTable(), address.getTable());
			assertEquals(addressType.getModbusDataType(), address.getModbusDataType());
		}
	}

	@Test
	public void testUnitId() {
		// The parser doesn't actually validate the range but meh...
		for (int i = 0; i < Byte.MAX_VALUE; i++) {
			String addressString = String.format("%s.HR2000", i);
			ModbusAddress address = ModbusAddress.parse(addressString);

			assertNotNull(String.format("Expected %s to parse.", addressString), address);
			assertEquals(i, address.getUnitId());
		}
	}

	@Test
	public void testBit_PeriodNotation() {
		for (int i = 0; i < 32; i++) {
			String addressString = String.format("HR2000.%s", i);
			ModbusAddress address = ModbusAddress.parse(addressString);

			assertNotNull(String.format("Expected %s to parse.", addressString), address);
			assertEquals(i, address.getBit());
		}
	}

	@Test
	public void testBit_SlashNotation() {
		for (int i = 0; i < 32; i++) {
			String addressString = String.format("HR2000/%s", i);
			ModbusAddress address = ModbusAddress.parse(addressString);

			assertNotNull(String.format("Expected %s to parse.", addressString), address);
			assertEquals(i, address.getBit());
		}
	}

	@Test
	public void test_String() {
		for (int i = 0; i < 2000; i++) {
			String addressString = String.format("HRS%s:50", i);
			ModbusAddress address = ModbusAddress.parse(addressString);

			assertNotNull(String.format("Expected %s to parse.", addressString), address);
		}
	}

	@Test
	public void testInvalid() {
		assertNull(ModbusAddress.parse("omgwtfbbq"));
		assertNull(ModbusAddress.parse("x.HR2000"));
		assertNull(ModbusAddress.parse("HRX2000"));
		assertNull(ModbusAddress.parse("HR2000f3"));
		assertNull(ModbusAddress.parse(""));
		assertNull(ModbusAddress.parse("3.FF2000"));
		assertNull(ModbusAddress.parse("HR2000/Z"));
	}
}
