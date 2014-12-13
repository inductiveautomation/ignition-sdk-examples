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
package com.inductiveautomation.xopc.drivers.modbus2.requests;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.opcua.types.StatusCode;
import com.inductiveautomation.opcua.types.Variant;
import com.inductiveautomation.xopc.driver.api.items.WriteItem;
import com.inductiveautomation.xopc.driver.api.requests.ReceiveAction;
import com.inductiveautomation.xopc.driver.util.ByteUtilities;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ApplicationData;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.ChannelWriter;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.CommunicationCallback;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.RequestOffsets;
import com.inductiveautomation.xopc.drivers.modbus2.structs.FunctionCode;
import com.inductiveautomation.xopc.drivers.modbus2.structs.WriteMultipleCoils;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.ModbusResponseException;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.ReadException;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.WriteMultipleCoilsResponseReader;
import com.inductiveautomation.xopc.drivers.modbus2.structs.writers.WriteMultipleCoilsRequestWriter;

public class WriteMultipleCoilsRequest extends AbstractModbusWriteRequest {

	private static final WriteMultipleCoilsRequestWriter requestWriter = new WriteMultipleCoilsRequestWriter();
	private static final WriteMultipleCoilsResponseReader responseReader = new WriteMultipleCoilsResponseReader();

	private final WriteMultipleCoils.Request request;

	public WriteMultipleCoilsRequest(
			List<WriteItem> items,
			ChannelWriter channelWriter,
			ModbusTransport transport,
			boolean zeroBased,
			byte unitId,
			int timeout,
			Logger log,
			CommunicationCallback communicationCallback) {
		super(items, channelWriter, transport, zeroBased, unitId, timeout, log, communicationCallback);

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		short startAddress = (short) offsets.getStartAddress();
		short quantity = (short) offsets.getLength();

		if (zeroBased) {
			startAddress--;
		}

		byte byteCount = (byte) (offsets.getLength() / 8);
		if (offsets.getLength() % 8 != 0) {
			byteCount++;
		}

		byte[] values = new byte[byteCount];

		for (int i = 0; i < items.size(); i++) {
			WriteItem item = items.get(i);
			Variant variant = item.getWriteValue();
			boolean b = (Boolean) TypeUtilities.coerce(variant.getValue(), Boolean.class);
			byte value = (byte) (b ? 1 : 0);

			values[i / 8] |= (value << (i % 8));
		}

		request = new WriteMultipleCoils.Request(startAddress, quantity, byteCount, values);
	}

	@Override
	protected ApplicationData getApplicationData() {
		return new ApplicationData() {
			@Override
			public void write(ByteBuffer buffer) {
				requestWriter.write(request, buffer);
			}

			@Override
			public int length() {
				return request.length();
			}
		};
	}

	@Override
	protected ReceiveAction handleResponse(ByteBuffer buffer) {
		try {
			WriteMultipleCoils.Response response = responseReader.read(buffer);

			FunctionCode functionCode = response.getFunctionCode();
			short startAddress = response.getStartAddress();
			short quantity = response.getQuantity();

			if (functionCode == request.getFunctionCode() &&
					startAddress == request.getStartAddress() &&
					quantity == request.getQuantity()) {
				setItemStatus(StatusCode.GOOD);
			} else {
				setItemStatus(StatusCode.BAD);

				log.error("Response fields did not mirror request fields.");
			}
		} catch (ModbusResponseException e) {
			setItemStatus(StatusCode.BAD);

			log.error(String.format("Received response with ExceptionCode: %s.", e.getResponse().getExceptionCode()));
		} catch (ReadException e) {
			setItemStatus(StatusCode.BAD);

			log.error(String.format("Error reading response at field \"%s\". Buffer=%s.",
					e.getField(), ByteUtilities.toString(e.getBs())));
		}

		return ReceiveAction.Done;
	}

}
