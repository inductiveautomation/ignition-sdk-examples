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
package com.inductiveautomation.xopc.drivers.modbus2.requests.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.inductiveautomation.xopc.driver.api.items.ReadItem;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.RequestOffsets;
import com.inductiveautomation.xopc.drivers.modbus2.util.TestReadItem;

public class RequestOffsetsTest {

	private List<ReadItem> items;

	@Before
	public void setUp() {
		items = new ArrayList<ReadItem>();
	}

	@Test
	public void testGetStartAddress() {
		items.add(new TestReadItem(ModbusAddress.parse("HR1")));
		items.add(new TestReadItem(ModbusAddress.parse("HR2")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(1, offsets.getStartAddress());
	}

	@Test
	public void testGetEndAddress_OneItem() {
		items.add(new TestReadItem(ModbusAddress.parse("HR1")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(1, offsets.getEndAddress());
	}

	@Test
	public void testGetEndAddress_MultipleItems() {
		items.add(new TestReadItem(ModbusAddress.parse("HR1")));
		items.add(new TestReadItem(ModbusAddress.parse("HR3")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(3, offsets.getEndAddress());
	}

	@Test
	public void testGetEndAddress_OneMultiRegisterValue() {
		items.add(new TestReadItem(ModbusAddress.parse("HRF1")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(2, offsets.getEndAddress());
	}

	@Test
	public void testGetEndAddress_OverlappingMultiRegisterValues() {
		items.add(new TestReadItem(ModbusAddress.parse("HRF1")));
		items.add(new TestReadItem(ModbusAddress.parse("HRF2")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(3, offsets.getEndAddress());
	}

	@Test
	public void testGetEndAddress_OneStringValue() {
		items.add(new TestReadItem(ModbusAddress.parse("HRS1:14")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(7, offsets.getEndAddress());
	}

	@Test
	public void testGetEndAddress_OverlappingStringValues() {
		items.add(new TestReadItem(ModbusAddress.parse("HRS1:14")));
		items.add(new TestReadItem(ModbusAddress.parse("HRS4:10")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(8, offsets.getEndAddress());
	}

	@Test
	public void testGetLength_OneItem() {
		items.add(new TestReadItem(ModbusAddress.parse("HR1")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(1, offsets.getLength());
	}
	
	@Test
	public void testGetLength_MultipleItems() {
		items.add(new TestReadItem(ModbusAddress.parse("HR1")));
		items.add(new TestReadItem(ModbusAddress.parse("HR2")));
		items.add(new TestReadItem(ModbusAddress.parse("HR3")));
		
		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		assertEquals(3, offsets.getLength());
	}
	
	@Test
	public void testGetLength_OneStringValue() {
		items.add(new TestReadItem(ModbusAddress.parse("HRS1:14")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();
		
		assertEquals(7, offsets.getLength());
	}
	
	@Test
	public void testGetLength_OverlappingStringValues() {
		items.add(new TestReadItem(ModbusAddress.parse("HRS1:14")));
		items.add(new TestReadItem(ModbusAddress.parse("HRS4:10")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();
		
		assertEquals(8, offsets.getLength());
	}
	
	@Test
	public void testGetLength_MixedValues() {
		items.add(new TestReadItem(ModbusAddress.parse("HR1")));
		items.add(new TestReadItem(ModbusAddress.parse("HR3")));
		items.add(new TestReadItem(ModbusAddress.parse("HRF5")));

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();
		
		assertEquals(6, offsets.getLength());
	}

	@Test
	public void testGetLength_OneCoil() {
		items.add(new TestReadItem(ModbusAddress.parse("C1")));
		
		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();
		
		assertEquals(1, offsets.getLength());
	}
	
	@Test
	public void testGetLength_MultipleCoils() {
		items.add(new TestReadItem(ModbusAddress.parse("C1")));
		items.add(new TestReadItem(ModbusAddress.parse("C2")));
		items.add(new TestReadItem(ModbusAddress.parse("C3")));
		items.add(new TestReadItem(ModbusAddress.parse("C4")));
		
		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();
		
		assertEquals(4, offsets.getLength());
	}
	
}
