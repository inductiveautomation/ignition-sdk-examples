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

import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.inductiveautomation.opcua.types.DataValue;
import com.inductiveautomation.opcua.types.StatusCode;
import com.inductiveautomation.xopc.driver.api.DriverState;
import com.inductiveautomation.xopc.driver.api.items.ReadItem;
import com.inductiveautomation.xopc.driver.api.requests.FailureType;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.ChannelWriter;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.CommunicationCallback;
import com.inductiveautomation.xopc.drivers.modbus2.structs.ExceptionCode;

public abstract class AbstractModbusReadRequest extends AbstractModbusRequest<ReadItem> {

	/**
	 * The number of times to retry this request, assuming the {@link com.inductiveautomation.xopc.drivers.modbus2.structs.ExceptionCode} is one in the
	 * set {@value #RETRYABLE_EXCEPTION_CODES}, before setting bad quality on this request's items.
	 */
	public static final int MAX_EXCEPTION_RESPONSES_TO_RETRY = 1;

	/**
	 * The set of {@link com.inductiveautomation.xopc.drivers.modbus2.structs.ExceptionCode}s that will increment a count towards
	 * {@value #MAX_EXCEPTION_RESPONSES_TO_RETRY} before setting bad quality on this request's
	 * items.
	 */
	public static final EnumSet<ExceptionCode> RETRYABLE_EXCEPTION_CODES = EnumSet.of(
			ExceptionCode.GatewayPathUnavailable,
			ExceptionCode.GatewayTargetDeviceFailToRespond,
			ExceptionCode.SlaveDeviceBusy,
			ExceptionCode.SlaveDeviceFailure);

	/**
	 * Tracks the number of consecutive responses containing {@link com.inductiveautomation.xopc.drivers.modbus2.structs.ExceptionCode}'s allowed by
	 * {@value #RETRYABLE_EXCEPTION_CODES}. Any other response, success or error, resets the count.
	 */
	protected volatile int exceptionResponseCount = 0;

	public AbstractModbusReadRequest(
			List<ReadItem> items,
			ChannelWriter channelWriter,
			ModbusTransport transport,
			boolean zeroBased,
			byte unitId,
			int timeout,
			Logger log,
			CommunicationCallback communicationCallback) {
		super(items, channelWriter, transport, zeroBased, unitId, timeout, log, communicationCallback);
	}

	@Override
	public void fail(FailureType type, DriverState state, Exception ex) {
		log.warn("Request failed. FailureType==" + type, ex);

		DataValue value = new DataValue(new StatusCode(type.getStatusSubCode(), true));

		setItemValues(value);

		super.fail(type, state, ex);
	}

	protected void setItemValues(DataValue value) {
		for (ReadItem item : items) {
			if (item != null) {
				item.setValue(value);
			}
		}
	}

}
