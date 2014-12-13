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

import org.apache.log4j.Logger;

import com.inductiveautomation.xopc.driver.api.items.ReadItem;
import com.inductiveautomation.xopc.driver.api.requests.Request;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;
import com.inductiveautomation.xopc.drivers.modbus2.requests.ReadCoilsRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.ReadDiscreteInputsRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.ReadHoldingRegistersRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.ReadInputRegistersRequest;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.ChannelWriter;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.CommunicationCallback;

public class ReadRequestFactory {

	private final ChannelWriter channelWriter;
	private final ModbusTransportFactory transportFactory;
	private final boolean zeroBased;
	private final int timeout;
	private final Logger log;
	private final boolean swapWords;
	private final boolean reverseStringByteOrder;
	private final CommunicationCallback communicationCallback;

	public ReadRequestFactory(
			ChannelWriter channelWriter,
			ModbusTransportFactory transportFactory,
			boolean zeroBased,
			int timeout,
			Logger log,
			boolean swapWords,
			boolean reverseStringByteOrder,
			CommunicationCallback communicationCallback) {
		this.channelWriter = channelWriter;
		this.transportFactory = transportFactory;
		this.zeroBased = zeroBased;
		this.timeout = timeout;
		this.log = log;
		this.swapWords = swapWords;
		this.reverseStringByteOrder = reverseStringByteOrder;
		this.communicationCallback = communicationCallback;
	}

	/**
	 * Creates an appropriate Request for the given items. Assumes that these items came from the
	 * read optimizer.
	 * 
	 * @param items
	 *            List of {@link com.inductiveautomation.xopc.driver.api.items.ReadItem}'s to include in the request.
	 * 
	 * @return An appropriate {@link com.inductiveautomation.xopc.driver.api.requests.Request} for the given items.
	 */
	public Request<byte[]> get(List<ReadItem> items) {
		ModbusAddress address = (ModbusAddress) items.get(0).getAddressObject();
		ModbusTable table = address.getTable();
		byte unitId = (byte) address.getUnitId();

		switch (table) {
			case Coils:
				return new ReadCoilsRequest(
						items,
						channelWriter,
						transportFactory.get(unitId),
						zeroBased,
						unitId,
						timeout,
						makeSubLogger(ReadCoilsRequest.class.getSimpleName()),
						communicationCallback);

			case DiscreteInputs:
				return new ReadDiscreteInputsRequest(
						items,
						channelWriter,
						transportFactory.get(unitId),
						zeroBased,
						unitId,
						timeout,
						makeSubLogger(ReadDiscreteInputsRequest.class.getSimpleName()),
						communicationCallback);

			case HoldingRegisters:
				return new ReadHoldingRegistersRequest(
						items,
						channelWriter,
						transportFactory.get(unitId),
						zeroBased,
						unitId,
						timeout,
						makeSubLogger(ReadHoldingRegistersRequest.class.getSimpleName()),
						swapWords,
						reverseStringByteOrder,
						communicationCallback);

			case InputRegisters:
				return new ReadInputRegistersRequest(
						items,
						channelWriter,
						transportFactory.get(unitId),
						zeroBased,
						unitId,
						timeout,
						makeSubLogger(ReadInputRegistersRequest.class.getSimpleName()),
						swapWords,
						reverseStringByteOrder,
						communicationCallback);

			default:
				throw new RuntimeException(String.format("Couldn't create request for %s.", table));
		}
	}

	private Logger makeSubLogger(String name) {
		return Logger.getLogger(String.format("%s.%s", log.getName(), name));
	}

}
