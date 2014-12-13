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
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import com.inductiveautomation.xopc.driver.api.DriverState;
import com.inductiveautomation.xopc.driver.api.requests.FailureType;
import com.inductiveautomation.xopc.driver.api.requests.ReceiveAction;
import com.inductiveautomation.xopc.driver.api.requests.Request;
import com.inductiveautomation.xopc.driver.api.requests.RequestPriority;
import com.inductiveautomation.xopc.driver.util.ByteUtilities;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ApplicationData;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.RxPacket;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.TxPacket;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.ChannelWriter;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.CommunicationCallback;
import com.inductiveautomation.xopc.drivers.modbus2.util.Source;

/**
 * @param <T>
 *            ReadItem for reads, WriteItem for writes.
 * 
 * @author Kevin Herron (kevin@inductiveautomation.com)
 */
public abstract class AbstractModbusRequest<T> implements Request<byte[]> {

	private final AtomicReference<ByteBuffer> lastSentBuffer = new AtomicReference<ByteBuffer>();

	protected final List<T> items;
	protected final ChannelWriter channelWriter;
	protected final ModbusTransport transport;
	protected final boolean zeroBased;
	protected final byte unitId;
	protected final int timeout;
	protected final Logger log;
	protected final CommunicationCallback communicationCallback;

	public AbstractModbusRequest(
			List<T> items,
			ChannelWriter channelWriter,
			ModbusTransport transport,
			boolean zeroBased,
			byte unitId,
			int timeout,
			Logger log,
			CommunicationCallback communicationCallback) {
		this.items = items;
		this.channelWriter = channelWriter;
		this.transport = transport;
		this.zeroBased = zeroBased;
		this.unitId = unitId;
		this.timeout = timeout;
		this.log = log;
		this.communicationCallback = communicationCallback;

		transport.setApplicationDataSource(new Source<ApplicationData>() {
			@Override
			public ApplicationData get() {
				return getApplicationData();
			}
		});
	}

	/**
	 * @return The ApplicationData that makes up this request. This will likely be the struct for
	 *         whatever type of request this is.
	 */
	protected abstract ApplicationData getApplicationData();

	/**
	 * Handle the response to this request. The buffer has already been positioned past the protocol
	 * data to the response data.
	 * 
	 * @param buffer
	 *            ByteBuffer containing the response data.
	 * 
	 * @return A ReceiveAction.
	 */
	protected abstract ReceiveAction handleResponse(ByteBuffer buffer);

	@Override
	public Object getKey() {
		return transport.getKey();
	}

	@Override
	public boolean sendMessage() {
		TxPacket packet = transport.next();

		ByteBuffer packetBuffer = packet.getBuffer();
		packetBuffer.position(packet.length());
		packetBuffer.flip();

		if (log.isTraceEnabled()) {
			log.trace(String.format("Sending message: %s", ByteUtilities.toString(packetBuffer)));
		}

		channelWriter.writeToChannel(packetBuffer);

		lastSentBuffer.set(packetBuffer);

		return true;
	}

	@Override
	public ReceiveAction receiveMessage(byte[] message, Object key) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("Received message: %s", ByteUtilities.toString(message)));
		}

		communicationCallback.notifyCommunicationSuccess();

		try {
			RxPacket packet = transport.getProtocolStack().receive(message);

			return handleResponse(packet.dataBuffer());
		} catch (Throwable t) {
			String errorMessage = String.format(
					"Uncaught Throwable handling response. key=%s, req=%s, rsp=%s",
					key, ByteUtilities.toString(lastSentBuffer.get()), ByteUtilities.toString(message));

			log.error(errorMessage, t);

			return ReceiveAction.Done;
		}
	}

	@Override
	public void fail(FailureType type, DriverState state, Exception ex) {
		if (type == FailureType.TIMEOUT) {
			communicationCallback.notifyCommunicationTimeout();
		}
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	public boolean isRetryAllowed() {
		return false;
	}

	@Override
	public RequestPriority getPriority() {
		return RequestPriority.LOW;
	}

}
