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
package com.inductiveautomation.xopc.drivers.modbus2.factories;

import java.util.List;

import com.inductiveautomation.ignition.common.util.LoggerEx;
import org.apache.log4j.Logger;

import com.inductiveautomation.xopc.driver.api.items.WriteItem;
import com.inductiveautomation.xopc.driver.api.requests.Request;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;
import com.inductiveautomation.xopc.drivers.modbus2.requests.MaskWriteRegisterRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.WriteMultipleCoilsRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.WriteMultipleRegistersRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.WriteSingleCoilRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.WriteSingleRegisterRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.ChannelWriter;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.CommunicationCallback;

public class WriteRequestFactory {

	private final ChannelWriter channelWriter;
	private final ModbusTransportFactory transportFactory;
	private final boolean zeroBased;
	private final int timeout;
	private final LoggerEx log;
	private final boolean swapWords;
	private final boolean rightJustifyStrings;
	private final boolean reverseStringByteOrder;
	private final CommunicationCallback communicationCallback;

	public WriteRequestFactory(
        ChannelWriter channelWriter,
        ModbusTransportFactory transportFactory,
        boolean zeroBased,
        int timeout,
        LoggerEx log,
        boolean swapWords,
        boolean rightJustifyStrings,
        boolean reverseStringByteOrder,
        CommunicationCallback communicationCallback) {
		this.channelWriter = channelWriter;
		this.transportFactory = transportFactory;
		this.zeroBased = zeroBased;
		this.timeout = timeout;
		this.log = log;
		this.swapWords = swapWords;
		this.rightJustifyStrings = rightJustifyStrings;
		this.reverseStringByteOrder = reverseStringByteOrder;
		this.communicationCallback = communicationCallback;
	}

	public Request<byte[]> get(List<WriteItem> items) {
		ModbusAddress address = (ModbusAddress) items.get(0).getAddressObject();
		ModbusTable table = address.getTable();

		switch (table) {
			case Coils:
				return getCoilRequest(items);

			case HoldingRegisters:
				return getHoldingRegisterRequest(items);

			case DiscreteInputs:
			case InputRegisters:
				// These should have been stopped from getting here by both the OPC server, assuming
				// we set these nodes to be not writable in ModbusDriver#buildNode(), as well as by
				// the optimizer at this point. Time to complain loudly.
				throw new RuntimeException(String.format("Cannot create write request for non-writable %s.", table));

			default:
				throw new RuntimeException(String.format("Cannot create write request for %s.", table));
		}
	}

	private Request<byte[]> getCoilRequest(List<WriteItem> items) {
		ModbusAddress address = (ModbusAddress) items.get(0).getAddressObject();
		byte unitId = (byte) address.getUnitId();

		if (items.size() == 1) {
			return new WriteSingleCoilRequest(
					items,
					channelWriter,
					transportFactory.get(unitId),
					zeroBased,
					unitId,
					timeout,
					makeSubLogger(WriteSingleCoilRequest.class.getSimpleName()),
					communicationCallback);
		} else {
			return new WriteMultipleCoilsRequest(
					items,
					channelWriter,
					transportFactory.get(unitId),
					zeroBased,
					unitId,
					timeout,
					makeSubLogger(WriteMultipleCoilsRequest.class.getSimpleName()),
					communicationCallback);
		}
	}

	private Request<byte[]> getHoldingRegisterRequest(List<WriteItem> items) {
		ModbusAddress address = (ModbusAddress) items.get(0).getAddressObject();
		byte unitId = (byte) address.getUnitId();

		if (address.getBit() != -1) {
			return new MaskWriteRegisterRequest(
					items,
					channelWriter,
					transportFactory.get(unitId),
					zeroBased,
					unitId,
					timeout,
					makeSubLogger(MaskWriteRegisterRequest.class.getSimpleName()),
					swapWords,
					communicationCallback);
		}

		if (items.size() == 1 && address.getAddressSpan() == 1) {
			return new WriteSingleRegisterRequest(
					items,
					channelWriter,
					transportFactory.get(unitId),
					zeroBased,
					unitId,
					timeout,
					makeSubLogger(WriteSingleRegisterRequest.class.getSimpleName()),
					rightJustifyStrings,
					reverseStringByteOrder,
					communicationCallback);
		} else {
			return new WriteMultipleRegistersRequest(
					items,
					channelWriter,
					transportFactory.get(unitId),
					zeroBased,
					unitId,
					timeout,
					makeSubLogger(WriteMultipleRegistersRequest.class.getSimpleName()),
					swapWords,
					rightJustifyStrings,
					reverseStringByteOrder,
					communicationCallback);
		}
	}

	private Logger makeSubLogger(String name) {
		return Logger.getLogger(String.format("%s.%s", log.getName(), name));
	}

}
