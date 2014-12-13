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

import com.inductiveautomation.opcua.types.DataValue;
import com.inductiveautomation.opcua.types.StatusCode;
import com.inductiveautomation.xopc.driver.api.items.ReadItem;
import com.inductiveautomation.xopc.driver.api.requests.ReceiveAction;
import com.inductiveautomation.xopc.driver.util.ByteUtilities;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ApplicationData;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;
import com.inductiveautomation.xopc.drivers.modbus2.requests.handlers.CoilAndDiscreteInputReadHandler;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.ChannelWriter;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.CommunicationCallback;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.RequestOffsets;
import com.inductiveautomation.xopc.drivers.modbus2.structs.ExceptionCode;
import com.inductiveautomation.xopc.drivers.modbus2.structs.ReadDiscreteInputs;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.ModbusResponseException;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.ReadDiscreteInputsResponseReader;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.ReadException;
import com.inductiveautomation.xopc.drivers.modbus2.structs.writers.ReadRequestWriter;

public class ReadDiscreteInputsRequest extends AbstractModbusReadRequest {

	private static final ReadRequestWriter requestWriter = new ReadRequestWriter();
	private static final ReadDiscreteInputsResponseReader responseReader = new ReadDiscreteInputsResponseReader();

	private final RequestOffsets requestOffsets;
	private final ReadDiscreteInputs.Request request;
	private final CoilAndDiscreteInputReadHandler handler;

	public ReadDiscreteInputsRequest(
			List<ReadItem> items,
			ChannelWriter channelWriter,
			ModbusTransport transport,
			boolean zeroBased,
			byte unitId,
			int timeout,
			Logger log,
			CommunicationCallback communicationCallback) {
		super(items, channelWriter, transport, zeroBased, unitId, timeout, log, communicationCallback);

		requestOffsets = new RequestOffsets.Calculator(items).calculate();

		short startAddress = (short) requestOffsets.getStartAddress();
		short quantity = (short) (requestOffsets.getEndAddress() - requestOffsets.getStartAddress() + 1);

		if (zeroBased) {
			startAddress--;
		}

		request = new ReadDiscreteInputs.Request(startAddress, quantity);

		handler = new CoilAndDiscreteInputReadHandler(items, requestOffsets);
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
		ReadDiscreteInputs.Response response = null;

		try {
			response = responseReader.read(buffer);

			handler.handle(response.getInputStatus());
		} catch (ModbusResponseException e) {
			ExceptionCode exceptionCode = e.getResponse().getExceptionCode();

			if (RETRYABLE_EXCEPTION_CODES.contains(exceptionCode)) {
				exceptionResponseCount++;

				if (exceptionResponseCount <= MAX_EXCEPTION_RESPONSES_TO_RETRY) {
					return ReceiveAction.PostAgain;
				}
			}

			setItemValues(new DataValue(StatusCode.BAD));

			log.error(String.format("Received response with ExceptionCode: %s.", e.getResponse().getExceptionCode()));
		} catch (ReadException e) {
			setItemValues(new DataValue(StatusCode.BAD));

			log.error(String.format("Error reading response at field \"%s\". Buffer=%s.",
					e.getField(), ByteUtilities.toString(e.getBs())));
		}

		exceptionResponseCount = 0;

		return ReceiveAction.Done;
	}

}
