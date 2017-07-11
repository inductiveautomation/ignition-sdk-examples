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

import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.xopc.driver.api.items.ReadItem;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints.ConstraintProcessor;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints.ModbusTableConstraint;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints.UnitIdConstraint;
import com.inductiveautomation.xopc.drivers.modbus2.util.ModbusAddressComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

public class ModbusReadOptimizer {

	private final TableOptimizer tableOptimizer = new TableOptimizer();

	private int maxCoils = 2000;
	private int maxDiscreteInputs = 2000;
	private int maxHoldingRegisters = 125;
	private int maxInputRegisters = 125;
	private boolean spanGaps = true;
	private boolean readMultipleRegistersRequestAllowed = true;
	private boolean readMultipleCoilsAllowed = true;
	private boolean readMultipleDiscreteInputsAllowed = true;

	private final LoggerEx log;

	ModbusReadOptimizer() {
		log = LoggerEx.newBuilder().build(getClass().getSimpleName());
	}

	public ModbusReadOptimizer(
        int maxCoils,
        int maxDiscreteInputs,
        int maxHoldingRegisters,
        int maxInputRegisters,
        boolean spanGaps,
        boolean readMultipleRegistersRequestAllowed,
        boolean readMultipleCoilsAllowed,
        boolean readMultipleDiscreteInputsAllowed,
        LoggerEx log) {
		this.maxCoils = maxCoils;
		this.maxDiscreteInputs = maxDiscreteInputs;
		this.maxHoldingRegisters = maxHoldingRegisters;
		this.maxInputRegisters = maxInputRegisters;
		this.spanGaps = spanGaps;
		this.readMultipleRegistersRequestAllowed = readMultipleRegistersRequestAllowed;
		this.readMultipleCoilsAllowed = readMultipleCoilsAllowed;
		this.readMultipleDiscreteInputsAllowed = readMultipleDiscreteInputsAllowed;
		this.log = log;
	}

	/**
	 * Optimize the given {@link com.inductiveautomation.xopc.driver.api.items.ReadItem}s. Expects they have all had
	 * {@link com.inductiveautomation.xopc.driver.api.items.DriverItem#setAddressObject(Object)} called on them with an appropriate
	 * {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress}.
	 * 
	 * @param toOptimize
	 *            The {@link com.inductiveautomation.xopc.driver.api.items.ReadItem}s to optimize.
	 * 
	 * @return Items optimized into lists that will fit into a single {@link com.inductiveautomation.xopc.driver.api.requests.Request}.
	 */
	public List<List<ReadItem>> optimizeReads(List<ReadItem> toOptimize) {
		List<List<ReadItem>> optimized = new ArrayList<>();

		long start = System.currentTimeMillis();

		ConstraintProcessor<ReadItem> cp = new ConstraintProcessor<ReadItem>();

		@SuppressWarnings("unchecked")
		List<List<ReadItem>> byUnitIdbyTable = cp.process(
				toOptimize,
				new UnitIdConstraint<ReadItem>(),
				new ModbusTableConstraint<ReadItem>());

		for (List<ReadItem> items : byUnitIdbyTable) {
			ModbusTable table = ((ModbusAddress) items.get(0).getAddressObject()).getTable();
			optimized.addAll(tableOptimizer.optimize(items, getMaxForTable(table)));
		}

		long end = System.currentTimeMillis();

		long delta = end - start;
		log.debug(String.format("Optimized %s items in %sms.", toOptimize.size(), delta));

		return optimized;
	}

	private int getMaxForTable(ModbusTable table) {
		switch (table) {
			case Coils:
				return readMultipleCoilsAllowed ? maxCoils : 1;
			case DiscreteInputs:
				return readMultipleDiscreteInputsAllowed ? maxDiscreteInputs : 1;
			case HoldingRegisters:
				return readMultipleRegistersRequestAllowed ? maxHoldingRegisters : 1;
			case InputRegisters:
				return readMultipleRegistersRequestAllowed ? maxInputRegisters : 1;
			default:
				throw new RuntimeException(String.format("Could not get max size for table %s.", table));
		}
	}

	private class TableOptimizer {

		public List<List<ReadItem>> optimize(List<ReadItem> items, int maxSize) {
			List<List<ReadItem>> requests = new ArrayList<List<ReadItem>>();
			List<ReadItem> currentRequest = new ArrayList<ReadItem>();

			Collections.sort(items, new ModbusAddressComparator());

			Iterator<ReadItem> iter = items.iterator();

			ReadItem firstItem = iter.next();
			ModbusAddress firstAddress = (ModbusAddress) firstItem.getAddressObject();
			int currentStartAddress = firstAddress.getStartAddress();
			currentRequest.add(firstItem);

			while (iter.hasNext()) {
				ReadItem nextItem = iter.next();
				ModbusAddress nextAddress = (ModbusAddress) nextItem.getAddressObject();

				int startAddress = nextAddress.getStartAddress();
				int span = nextAddress.getAddressSpan();

				if (spanGaps == false && startAddress + (span - 1) - currentStartAddress > 1) {
					requests.add(currentRequest);
					currentRequest = new ArrayList<ReadItem>();
					currentRequest.add(nextItem);
					currentStartAddress = startAddress;
				}

				else if (startAddress + span - currentStartAddress <= maxSize) {
					currentRequest.add(nextItem);
				}

				else {
					requests.add(currentRequest);
					currentRequest = new ArrayList<ReadItem>();
					currentRequest.add(nextItem);
					currentStartAddress = startAddress;
				}
			}

			if (!currentRequest.isEmpty()) {
				requests.add(currentRequest);
			}

			return requests;
		}

	}

}
