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
package com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.opcua.types.DataValue;
import com.inductiveautomation.opcua.types.NodeId;
import com.inductiveautomation.xopc.driver.api.items.ReadItem;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusDataType;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;
import org.apache.log4j.Logger;
import org.junit.Test;

public class ModbusReadOptimizerTest {

	@Test
	public void testUnitIdSplitsRequests() {
		List<ModbusAddress> addresses = new ArrayList<ModbusAddress>();
		for (int i = 1; i <= 5; i++) {
			addresses.add(ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, i)
									   .setUnitId(i)
									   .build());
		}

		List<ReadItem> items = new ArrayList<ReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		ModbusReadOptimizer optimizer = new ModbusReadOptimizer();
		List<List<ReadItem>> optimized = optimizer.optimizeReads(items);

		assertEquals("Should have been optimized into 5 requests.", 5, optimized.size());
	}

	@Test
	public void testSameUnitIdSameRequest() {
		List<ModbusAddress> addresses = new ArrayList<ModbusAddress>();
		for (int i = 1; i <= 10; i++) {
			addresses.add(ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, i)
									   .setUnitId(i % 2)
									   .build());
		}

		List<ReadItem> items = new ArrayList<ReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		ModbusReadOptimizer optimizer = new ModbusReadOptimizer();
		List<List<ReadItem>> optimized = optimizer.optimizeReads(items);

		assertEquals("Should have been optimized into 2 requests.", 2, optimized.size());
	}

	@Test
	public void testDefaultMaxCoils() {
		List<ModbusAddress> addresses = new ArrayList<ModbusAddress>();
		for (int i = 1; i <= 4000; i++) {
			addresses.add(ModbusAddress.builder(ModbusTable.Coils, ModbusDataType.Boolean, i).build());
		}

		List<ReadItem> items = new ArrayList<ReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		ModbusReadOptimizer optimizer = new ModbusReadOptimizer();
		List<List<ReadItem>> optimized = optimizer.optimizeReads(items);

		assertEquals("Should have been optimized into 2 requests.", 2, optimized.size());
	}

	@Test
	public void testCustomMaxCoils() {
		List<ModbusAddress> addresses = new ArrayList<ModbusAddress>();
		for (int i = 1; i <= 4000; i++) {
			addresses.add(ModbusAddress.builder(ModbusTable.Coils, ModbusDataType.Boolean, i).build());
		}

		List<ReadItem> items = new ArrayList<ReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		ModbusReadOptimizer optimizer = new ModbusReadOptimizer(1000, 2000, 125, 125, true, true, true, true,
																LoggerEx.newBuilder().build("testCustomMaxCoils()"));
		List<List<ReadItem>> optimized = optimizer.optimizeReads(items);

		assertEquals("Should have been optimized into 4 requests.", 4, optimized.size());
	}

	@Test
	public void testMultiRegisterDataType() {
		List<ModbusAddress> addresses = new ArrayList<ModbusAddress>();
		for (int i = 1; i <= 248; i += 2) {
			addresses.add(ModbusAddress.builder(ModbusTable.InputRegisters, ModbusDataType.Int32, i).build());
		}

		List<ReadItem> items = new ArrayList<ReadItem>();
		for (ModbusAddress address : addresses) {
			items.add(new TestReadItem(address));
		}

		ModbusReadOptimizer optimizer = new ModbusReadOptimizer();
		List<List<ReadItem>> optimized = optimizer.optimizeReads(items);

		assertEquals("Should have been optimized into 2 requests.", 2, optimized.size());
	}

	@Test
	public void testGapSpanningOn() {
		ModbusAddress addr1 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 0).build();
		ModbusAddress addr2 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 2).build();
		ModbusAddress addr3 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 4).build();
		ModbusAddress addr4 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 5).build();

		List<ReadItem> items = new ArrayList<ReadItem>();
		items.add(new TestReadItem(addr1));
		items.add(new TestReadItem(addr2));
		items.add(new TestReadItem(addr3));
		items.add(new TestReadItem(addr4));

		ModbusReadOptimizer optimizer = new ModbusReadOptimizer();
		List<List<ReadItem>> optimized = optimizer.optimizeReads(items);

		assertEquals("Should have been optimized into 1 request.", 1, optimized.size());
	}

	@Test
	public void testGapSpanningOff() {
		ModbusAddress addr1 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 0).build();
		ModbusAddress addr2 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 2).build();
		ModbusAddress addr3 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 4).build();
		ModbusAddress addr4 = ModbusAddress.builder(ModbusTable.HoldingRegisters, ModbusDataType.Int16, 5).build();

		List<ReadItem> items = new ArrayList<ReadItem>();
		items.add(new TestReadItem(addr1));
		items.add(new TestReadItem(addr2));
		items.add(new TestReadItem(addr3));
		items.add(new TestReadItem(addr4));

		ModbusReadOptimizer optimizer = new ModbusReadOptimizer(2000, 2000, 125, 125, false, true, true, true,
																LoggerEx.newBuilder().build("testGapSpanningOff"));
		List<List<ReadItem>> optimized = optimizer.optimizeReads(items);

		assertEquals("Should have been optimized into 3 requests.", 3, optimized.size());
	}

	private static class TestReadItem implements ReadItem {

		private final ModbusAddress modbusAddress;

		public TestReadItem(ModbusAddress modbusAddress) {
			this.modbusAddress = modbusAddress;
		}

		@Override
		public String getAddress() {
			return modbusAddress.toString();
		}

		@Override
		public Object getAddressObject() {
			return modbusAddress;
		}

		@Override
		public void setAddressObject(Object obj) {
		}

		@Override
		public void setValue(DataValue value) {
		}

		@Override
		public NodeId getNodeId() {
			return NodeId.NULL;
		}

	}

}
