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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.inductiveautomation.xopc.driver.api.items.DriverItem;
import com.inductiveautomation.xopc.driver.api.items.WriteItem;
import com.inductiveautomation.xopc.driver.api.requests.Request;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints.BitWordConstraint;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints.ConstraintProcessor;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints.ModbusTableConstraint;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints.StartAddressConstraint;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.constraints.UnitIdConstraint;
import com.inductiveautomation.xopc.drivers.modbus2.util.ModbusAddressComparator;

/**
 * The following optimization rules apply:
 * 
 * <ul>
 * <li>Writes to contiguous coils can share a request.</li>
 * <li>Writes to contiguous holding registers can share a request.</li>
 * <li>Writes to bits inside the same register can share a request.</li>
 * <li>All other writes must be in their own request.</li>
 * </ul>
 * 
 * @author Kevin Herron (kevin@inductiveautomation.com)
 */
public class ModbusWriteOptimizer {

	private boolean writeMultipleCoilsRequestAllowed = true;
	private boolean writeMultipleRegistersRequestAllowed = true;

	private final Logger log;

	ModbusWriteOptimizer() {
		log = Logger.getLogger(getClass().getSimpleName());
	}

	public ModbusWriteOptimizer(
			boolean writeMultipleCoilsRequestAllowed,
			boolean writeMultipleRegistersRequestAllowed,
			Logger log) {
		this.writeMultipleCoilsRequestAllowed = writeMultipleCoilsRequestAllowed;
		this.writeMultipleRegistersRequestAllowed = writeMultipleRegistersRequestAllowed;
		this.log = log;
	}

	/**
	 * Optimize the given {@link com.inductiveautomation.xopc.driver.api.items.WriteItem}s. Expects they have all had
	 * {@link com.inductiveautomation.xopc.driver.api.items.DriverItem#setAddressObject(Object)} called on them with an appropriate
	 * {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress}.
	 * 
	 * @param toOptimize
	 *            The {@link com.inductiveautomation.xopc.driver.api.items.WriteItem}s to optimize.
	 * 
	 * @return Items optimized into lists that will fit into a single {@link com.inductiveautomation.xopc.driver.api.requests.Request}.
	 */
	public List<List<WriteItem>> optimizeWrites(List<WriteItem> toOptimize) {
		List<List<WriteItem>> optimized = new ArrayList<List<WriteItem>>();

		long start = System.currentTimeMillis();

		List<WriteItem> notWritable = new ArrayList<WriteItem>();
		notWritable.addAll(Collections2.filter(toOptimize, new Predicate<WriteItem>() {
			@Override
			public boolean apply(WriteItem item) {
				ModbusAddress address = (ModbusAddress) item.getAddressObject();
				return address.getTable() == ModbusTable.DiscreteInputs
						|| address.getTable() == ModbusTable.InputRegisters;
			}
		}));

		List<WriteItem> writableNonBit = new ArrayList<WriteItem>();
		writableNonBit.addAll(Collections2.filter(toOptimize, new Predicate<WriteItem>() {
			@Override
			public boolean apply(WriteItem item) {
				ModbusAddress address = (ModbusAddress) item.getAddressObject();
				return (address.getTable() == ModbusTable.Coils
						|| address.getTable() == ModbusTable.HoldingRegisters)
						&& address.getBit() == -1;
			}
		}));

		List<WriteItem> writableBit = new ArrayList<WriteItem>();
		writableBit.addAll(Collections2.filter(toOptimize, new Predicate<WriteItem>() {
			@Override
			public boolean apply(WriteItem item) {
				ModbusAddress address = (ModbusAddress) item.getAddressObject();
				return address.getTable() == ModbusTable.HoldingRegisters
						&& address.getBit() > -1;
			}
		}));

		optimized.addAll(optimizeNonBitWrites(writableNonBit));
		optimized.addAll(optimizeBitWrites(writableBit));

		if (notWritable.size() > 0) {
			// Drop these and warn about it. If the DI and IR nodes were created correctly in
			// buildNode(), that is, ACCESS_WRITE was not set, then we should never receive writes
			// for them.
			List<String> strings = new ArrayList<String>();
			for (WriteItem item : notWritable) {
				strings.add(item.getAddressObject() != null ? item.getAddressObject().toString() : item.toString());
			}
			log.warn(String
					.format("Received the following non-writable items for optimization: %s. This means write access was erroneously set.",
							strings));
		}

		long end = System.currentTimeMillis();

		long delta = end - start;
		log.debug(String.format("Optimized %s items in %sms.", toOptimize.size(), delta));

		return optimized;
	}

	private List<List<WriteItem>> optimizeNonBitWrites(List<WriteItem> writableNonBit) {
		List<List<WriteItem>> optimized = new ArrayList<List<WriteItem>>();

		ConstraintProcessor<WriteItem> cp = new ConstraintProcessor<WriteItem>();

		@SuppressWarnings("unchecked")
		List<List<WriteItem>> byUnitIdByTable = cp.process(
				writableNonBit,
				new UnitIdConstraint<WriteItem>(),
				new ModbusTableConstraint<WriteItem>());

		NonBitOptimizer optimizer = new NonBitOptimizer();

		for (List<WriteItem> items : byUnitIdByTable) {
			optimized.addAll(optimizer.optimize(items));
		}

		return optimized;
	}

	private List<List<WriteItem>> optimizeBitWrites(List<WriteItem> writableBit) {
		ConstraintProcessor<WriteItem> cp = new ConstraintProcessor<WriteItem>();

		@SuppressWarnings("unchecked")
		List<List<WriteItem>> optimized = cp.process(
				writableBit,
				new UnitIdConstraint<WriteItem>(),
				new StartAddressConstraint<WriteItem>(),
				new BitWordConstraint<WriteItem>());

		return optimized;
	}

	private class NonBitOptimizer {

		public List<List<WriteItem>> optimize(List<WriteItem> items) {
			// If we're here we can assume we've already been split but unitId and table. At this
			// point we're only working with coils and registers that are not addressing bits.

			ModbusAddress address = (ModbusAddress) items.get(0).getAddressObject();

			if (address.getTable() == ModbusTable.Coils &&
					writeMultipleCoilsRequestAllowed == false) {
				return Lists.partition(items, 1);
			}

			if (address.getTable() == ModbusTable.HoldingRegisters &&
					writeMultipleRegistersRequestAllowed == false) {
				return Lists.partition(items, 1);
			}

			List<List<WriteItem>> requests = new ArrayList<List<WriteItem>>();
			List<WriteItem> currentRequest = new ArrayList<WriteItem>();

			Collections.sort(items, new ModbusAddressComparator());

			Iterator<WriteItem> iter = items.iterator();

			WriteItem firstItem = iter.next();
			ModbusAddress firstAddress = (ModbusAddress) firstItem.getAddressObject();
			int currentStartAddress = firstAddress.getStartAddress();
			currentRequest.add(firstItem);

			while (iter.hasNext()) {
				WriteItem nextItem = iter.next();
				ModbusAddress nextAddress = (ModbusAddress) nextItem.getAddressObject();

				int startAddress = nextAddress.getStartAddress();
				int span = nextAddress.getAddressSpan();

				if (startAddress + (span - 1) - currentStartAddress > 1) {
					// Not contiguous
					requests.add(currentRequest);
					currentRequest = new ArrayList<WriteItem>();
					currentRequest.add(nextItem);
					currentStartAddress = startAddress;
				} else {
					// Contiguous
					currentRequest.add(nextItem);
				}
			}

			if (!currentRequest.isEmpty()) {
				requests.add(currentRequest);
			}

			return requests;
		}
	}

}
