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
package com.inductiveautomation.xopc.drivers.modbus2.requests.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusDataType;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.RequestOffsets;
import com.inductiveautomation.xopc.drivers.modbus2.util.TestReadItem;

public class RegisterReadHandlerTest {

	@Test
	public void testBCD16() {
		ModbusAddress[] addresses = new ModbusAddress[] {
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD16, 1).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD16, 2).build() };

		List<TestReadItem> items = new ArrayList<TestReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
		handle(items, data);

		short s1 = getValue(items.get(0), Short.class);
		assertEquals("Expected 102.", 102, s1);

		short s2 = getValue(items.get(1), Short.class);
		assertEquals("Expected 304.", 304, s2);
	}

	@Test
	public void testBCD32() {
		ModbusAddress[] addresses = new ModbusAddress[] {
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD32, 1).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD32, 3).build() };

		List<TestReadItem> items = new ArrayList<TestReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		byte[] data = new byte[] { 0x01, 0x22, 0x33, 0x44, 0x05, 0x66, 0x77, 0x78 };
		handle(items, data);

		int i1 = getValue(items.get(0), Integer.class);
		assertEquals("Expected 1223344.", 1223344, i1);

		int i2 = getValue(items.get(1), Integer.class);
		assertEquals("Expected 5667778.", 5667778, i2);
	}

	@Test
	public void test16Bit() {
		ModbusAddress[] addresses = new ModbusAddress[] {
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 1).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 2).build() };

		List<TestReadItem> items = new ArrayList<TestReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
		handle(items, data);

		short s1 = getValue(items.get(0), Short.class);
		assertEquals("Expected 0x0102.", 0x0102, s1);

		short s2 = getValue(items.get(1), Short.class);
		assertEquals("Expected 0x0304.", 0x0304, s2);
	}

	@Test
	public void test32Bit() {
		ModbusAddress[] addresses = new ModbusAddress[] {
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 1).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 3).build() };

		List<TestReadItem> items = new ArrayList<TestReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
		handle(items, data);

		int i1 = getValue(items.get(0), Integer.class);
		assertEquals("Expected 0x01020304.", 0x01020304, i1);

		int i2 = getValue(items.get(1), Integer.class);
		assertEquals("Expected 0x05060708.", 0x05060708, i2);
	}

	@Test
	public void testBCD16_WithBitRead() {
		ModbusAddress[] addresses = new ModbusAddress[] {
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD16, 1).setBit(0).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD16, 2).setBit(8).build() };

		List<TestReadItem> items = new ArrayList<TestReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		byte[] data = new byte[] { 0x00, 0x01, 0x02, 0x56 };
		handle(items, data);

		boolean b1 = getValue(items.get(0), Boolean.class);
		assertTrue("Expected true.", b1);

		boolean b2 = getValue(items.get(1), Boolean.class);
		assertTrue("Expected true.", b2);
	}

	@Test
	public void testBCD32_WithBitRead() {
		ModbusAddress[] addresses = new ModbusAddress[] {
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD32, 1).setBit(0).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD32, 3).setBit(8).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD32, 5).setBit(16).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.BCD32, 7).setBit(24).build() };

		List<TestReadItem> items = new ArrayList<TestReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		byte[] data = new byte[] { 0x00, 0x00, 0x00, 0x01, // (1<<0)
				0x00, 0x00, 0x02, 0x56, // (1<<8)
				0x00, 0x06, 0x55, 0x36, // (1<<16)
				0x16, 0x77, 0x72, 0x16 // (1<<24)
		};

		handle(items, data);

		boolean b1 = getValue(items.get(0), Boolean.class);
		assertTrue("Expected true.", b1);

		boolean b2 = getValue(items.get(1), Boolean.class);
		assertTrue("Expected true.", b2);

		boolean b3 = getValue(items.get(2), Boolean.class);
		assertTrue("Expected true.", b3);

		boolean b4 = getValue(items.get(3), Boolean.class);
		assertTrue("Expected true.", b4);
	}

	@Test
	public void test16Bit_WithBitRead() {
		ModbusAddress[] addresses = new ModbusAddress[] {
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 1).setBit(0).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 2).setBit(8).build() };

		List<TestReadItem> items = new ArrayList<TestReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		byte[] data = new byte[] { 0x00, 0x01, 0x01, 0x00 };
		handle(items, data);

		boolean b1 = getValue(items.get(0), Boolean.class);
		assertTrue("Expected true.", b1);

		boolean b2 = getValue(items.get(1), Boolean.class);
		assertTrue("Expected true.", b2);
	}

	@Test
	public void test32Bit_WithBitRead() {
		ModbusAddress[] addresses = new ModbusAddress[] {
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 1).setBit(0).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 3).setBit(8).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 5).setBit(16).build(),
				ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int32, 7).setBit(24).build() };

		List<TestReadItem> items = new ArrayList<TestReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		byte[] data = new byte[] { 0x00, 0x00, 0x00, 0x01, // (1<<0)
				0x00, 0x00, 0x01, 0x00, // (1<<8)
				0x00, 0x01, 0x00, 0x00, // (1<<16)
				0x01, 0x00, 0x00, 0x00 // (1<<24)
		};
		handle(items, data);

		boolean b1 = getValue(items.get(0), Boolean.class);
		assertTrue("Expected true.", b1);

		boolean b2 = getValue(items.get(1), Boolean.class);
		assertTrue("Expected true.", b2);

		boolean b3 = getValue(items.get(2), Boolean.class);
		assertTrue("Expected true.", b3);

		boolean b4 = getValue(items.get(3), Boolean.class);
		assertTrue("Expected true.", b4);
	}

	//	@Test
	//	public void testBCD32_WithSwappedWords() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void test32Bit_WithSwappedWords() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testBCD32_WithSwappedWords_AndBit() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void test32Bit_WithSwappedWords_AndBit() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testMixedTypes() {
	//		fail("Not yet implemented");
	//	}

	@SuppressWarnings("unchecked")
	private <T> T getValue(TestReadItem item, Class<T> clazz) {
		return (T) TypeUtilities.coerce(item.getValue().getValue().getValue(), clazz);
	}

	private void handle(List<TestReadItem> items, byte[] data) {
		handle(items, data, false, false);
	}

	private void handle(List<TestReadItem> items, byte[] data, boolean swapWords, boolean reverseStringByteOrder) {
		RequestOffsets requestOffsets = new RequestOffsets.Calculator(items).calculate();

		RegisterReadHandler handler = new RegisterReadHandler(items, requestOffsets, swapWords, reverseStringByteOrder);
		handler.handle(data);
	}

}
