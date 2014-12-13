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
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.opcua.types.StatusCode;
import com.inductiveautomation.opcua.types.Variant;
import com.inductiveautomation.xopc.driver.api.items.WriteItem;
import com.inductiveautomation.xopc.driver.api.requests.ReceiveAction;
import com.inductiveautomation.xopc.driver.util.BCDByteUtilities;
import com.inductiveautomation.xopc.driver.util.ByteUtilities;
import com.inductiveautomation.xopc.driver.util.UAByteUtilities;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusDataType;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ApplicationData;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.ChannelWriter;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.CommunicationCallback;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.RequestOffsets;
import com.inductiveautomation.xopc.drivers.modbus2.structs.FunctionCode;
import com.inductiveautomation.xopc.drivers.modbus2.structs.WriteMultipleRegisters;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.ModbusResponseException;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.ReadException;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.WriteMultipleRegistersResponseReader;
import com.inductiveautomation.xopc.drivers.modbus2.structs.writers.WriteMultipleRegistersRequestWriter;

public class WriteMultipleRegistersRequest extends AbstractModbusWriteRequest {

	private static final WriteMultipleRegistersRequestWriter requestWriter = new WriteMultipleRegistersRequestWriter();
	private static final WriteMultipleRegistersResponseReader responseReader = new WriteMultipleRegistersResponseReader();

	private final WriteMultipleRegisters.Request request;

	private final boolean swapWords;
	private final boolean rightJustifyStrings;
	private final boolean reverseStringByteOrder;

	public WriteMultipleRegistersRequest(
			List<WriteItem> items,
			ChannelWriter channelWriter,
			ModbusTransport transport,
			boolean zeroBased,
			byte unitId,
			int timeout,
			Logger log,
			boolean swapWords,
			boolean rightJustifyStrings,
			boolean reverseStringByteOrder,
			CommunicationCallback communicationCallback) {
		super(items, channelWriter, transport, zeroBased, unitId, timeout, log, communicationCallback);

		this.swapWords = swapWords;
		this.rightJustifyStrings = rightJustifyStrings;
		this.reverseStringByteOrder = reverseStringByteOrder;

		RequestOffsets offsets = new RequestOffsets.Calculator(items).calculate();

		// Multiply by two since each unit of length is one 16-bit register.
		ByteBuffer buffer = ByteBuffer.allocate(offsets.getLength() * 2);

		Iterator<? extends WriteItem> iter = items.iterator();

		while (iter.hasNext()) {
			WriteItem item = iter.next();
			ModbusAddress address = (ModbusAddress) item.getAddressObject();

			int offset = (address.getStartAddress() - offsets.getStartAddress()) * 2;
			buffer.position(offset);

			byte[] bs = getValue(item, address);
			buffer.put(bs);
		}

		short startAddress = (short) offsets.getStartAddress();
		short quantity = (short) offsets.getLength();
		byte byteCount = (byte) buffer.limit();
		byte[] values = buffer.array();

		if (zeroBased) {
			startAddress--;
		}

		request = new WriteMultipleRegisters.Request(startAddress, quantity, byteCount, values);
	}

	private byte[] getValue(WriteItem item, ModbusAddress address) {
		ModbusDataType modbusType = address.getModbusDataType();
		Variant variant = item.getWriteValue();

		switch (modbusType) {
			case BCD16:
				short bcd16 = (Short) TypeUtilities.coerce(variant.getValue(), Short.class);
				return BCDByteUtilities.get(ByteOrder.BIG_ENDIAN).fromShort(bcd16, 0xFFFF);

			case BCD32:
				int bcd32 = (Integer) TypeUtilities.coerce(variant.getValue(), Integer.class);
				byte[] bcd32bs = BCDByteUtilities.get(ByteOrder.BIG_ENDIAN).fromInt(bcd32, 0xFFFFFFFF);
				if (swapWords) {
					return ByteUtilities.swapWords(bcd32bs, 0);
				}
				return bcd32bs;

			case Int16:
			case UInt16:
				return UAByteUtilities.fromVariant(variant, ByteOrder.BIG_ENDIAN);

			case Int32:
			case UInt32:
			case Float:
				byte[] bs = UAByteUtilities.fromVariant(variant, ByteOrder.BIG_ENDIAN);
				if (swapWords) {
					return ByteUtilities.swapWords(bs, 0);
				}
				return bs;

			case Int64:
			case UInt64:
			case Double:
				byte[] bs64 = UAByteUtilities.fromVariant(variant, ByteOrder.BIG_ENDIAN);
				if (swapWords) {
					byte[] h1 = ByteUtilities.swapWords(bs64, 0);
					byte[] h2 = ByteUtilities.swapWords(bs64, 4);
					System.arraycopy(h2, 0, bs64, 0, 4);
					System.arraycopy(h1, 0, bs64, 4, 4);
				}
				return bs64;

			case String:
				String s = TypeUtilities.toString(variant.getValue());
				int addressLength = address.getStringLength();

				if (s.length() > addressLength) {
					s = s.substring(0, addressLength);
				}

				byte[] padding = new byte[addressLength - s.length()];
				Arrays.fill(padding, (byte) ' ');

				byte[] stringBytes = rightJustifyStrings ?
						ArrayUtils.addAll(padding, s.getBytes()) : ArrayUtils.addAll(s.getBytes(), padding);

				if (reverseStringByteOrder) {
					byte[] reversed = new byte[stringBytes.length];
					for (int i = 0; i < stringBytes.length; i += 2) {
						byte[] swapped = ByteUtilities.swapBytes(stringBytes, i);
						System.arraycopy(swapped, 0, reversed, i, swapped.length);
					}
					stringBytes = reversed;
				}

				return stringBytes;

			case Boolean:
				throw new RuntimeException(String.format("Writing %s to %s not allowed.", modbusType, address));

			default:
				throw new RuntimeException(String.format("Case for %s not defined.", modbusType));
		}
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
			WriteMultipleRegisters.Response response = responseReader.read(buffer);

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
