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
package com.inductiveautomation.xopc.drivers.modbus2.requests.handlers;

import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.List;

import com.inductiveautomation.opcua.types.DataValue;
import com.inductiveautomation.opcua.types.UInt16;
import com.inductiveautomation.opcua.types.UInt32;
import com.inductiveautomation.opcua.types.Variant;
import com.inductiveautomation.xopc.driver.api.items.ReadItem;
import com.inductiveautomation.xopc.driver.util.BCDByteUtilities;
import com.inductiveautomation.xopc.driver.util.ByteUtilities;
import com.inductiveautomation.xopc.driver.util.UAByteUtilities;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusDataType;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.RequestOffsets;

public class RegisterReadHandler {

	private final List<? extends ReadItem> items;
	private final RequestOffsets requestOffsets;
	private final boolean swapWords;
	private final boolean reverseStringByteOrder;

	public RegisterReadHandler(
			List<? extends ReadItem> items,
			RequestOffsets requestOffsets,
			boolean swapWords,
			boolean reverseStringByteOrder) {
		this.items = items;
		this.requestOffsets = requestOffsets;
		this.swapWords = swapWords;
		this.reverseStringByteOrder = reverseStringByteOrder;
	}

	public void handle(byte[] data) {
		Iterator<? extends ReadItem> iter = items.iterator();

		while (iter.hasNext()) {
			ReadItem item = iter.next();
			DataValue value = getValue(item, data);

			item.setValue(value);
		}
	}

	private DataValue getValue(ReadItem item, byte[] data) {
		ModbusAddress address = (ModbusAddress) item.getAddressObject();
		ModbusDataType modbusType = address.getModbusDataType();

		int offset = (address.getStartAddress() - requestOffsets.getStartAddress()) * 2;

		switch (modbusType) {
			case BCD16:
				return readBCD16(data, address, offset);

			case BCD32:
				return readBCD32(data, address, offset);

			case Int16:
			case UInt16:
				return read16Bit(data, address, offset);

			case Int32:
			case UInt32:
			case Float:
				return read32Bit(data, address, offset);

			case Int64:
			case UInt64:
			case Double:
				return read64Bit(data, address, offset);

			case String:
				return readString(data, address, offset);

			case Boolean:
				throw new RuntimeException(
						String.format("Should not be reading a %s in a %s.", modbusType, getClass().getSimpleName()));

			default:
				throw new RuntimeException(String.format("Case for %s not defined.", modbusType));
		}
	}

	private DataValue readBCD16(byte[] data, ModbusAddress address, int offset) {
		short bcd16 = BCDByteUtilities.get(ByteOrder.BIG_ENDIAN).getShort(data, offset);

		int bit = address.getBit();
		if (bit > -1) {
			bcd16 = (short) (bcd16 & (0x1 << (bit % 16)));
			return new DataValue(new Variant(bcd16 != 0));
		}

		return new DataValue(new Variant(new UInt16(bcd16)));
	}

	private DataValue readBCD32(byte[] data, ModbusAddress address, int offset) {
		byte[] bs = swapWords ? ByteUtilities.swapWords(data, offset) : data;
		offset = swapWords ? 0 : offset;

		int bcd32 = BCDByteUtilities.get(ByteOrder.BIG_ENDIAN).getInt(bs, offset);

		int bit = address.getBit();
		if (bit > -1) {
			bcd32 = (bcd32 & (0x1 << (bit % 32)));
			return new DataValue(new Variant(bcd32 != 0));
		}

		return new DataValue(new Variant(new UInt32(bcd32)));
	}

	private DataValue read16Bit(byte[] data, ModbusAddress address, int offset) {
		int bit = address.getBit();
		if (bit > -1) {
			short s = ByteUtilities.get(ByteOrder.BIG_ENDIAN).getShort(data, offset);
			return new DataValue(new Variant(((s >>> (bit % 16)) & 0x1) == 1));
		}

		return UAByteUtilities.getDataValue(
				data,
				offset,
				ByteOrder.BIG_ENDIAN,
				address.getUADataType());
	}

	private DataValue read32Bit(byte[] data, ModbusAddress address, int offset) {
		byte[] bs = swapWords ? ByteUtilities.swapWords(data, offset) : data;
		offset = swapWords ? 0 : offset;

		int bit = address.getBit();
		if (bit > -1) {
			int i = ByteUtilities.get(ByteOrder.BIG_ENDIAN).getInt(bs, offset);
			return new DataValue(new Variant(((i >>> (bit % 32)) & 0x1) == 1));
		}

		return UAByteUtilities.getDataValue(
				bs,
				offset,
				ByteOrder.BIG_ENDIAN,
				address.getUADataType());
	}

	private DataValue read64Bit(byte[] data, ModbusAddress address, int offset) {
		byte[] bs;

		if (swapWords) {
			bs = new byte[8];
			byte[] h1 = ByteUtilities.swapWords(data, offset + 0);
			byte[] h2 = ByteUtilities.swapWords(data, offset + 4);
			System.arraycopy(h2, 0, bs, 0, 4);
			System.arraycopy(h1, 0, bs, 4, 4);
		} else {
			bs = data;
		}

		offset = swapWords ? 0 : offset;

		int bit = address.getBit();
		if (bit > -1) {
			long i = ByteUtilities.get(ByteOrder.BIG_ENDIAN).getLong(bs, offset);
			return new DataValue(new Variant(((i >>> (bit % 64)) & 0x1) == 1));
		}

		return UAByteUtilities.getDataValue(
				bs,
				offset,
				ByteOrder.BIG_ENDIAN,
				address.getUADataType());
	}

	private DataValue readString(byte[] data, ModbusAddress address, int offset) {
		// Make sure we don't underflow for some reason. Maybe they lowered Max HR per Request and
		// we don't have as many bytes as the stringLength specified...
		int length = Math.min(address.getStringLength(), data.length - offset);

		String s = reverseStringByteOrder ? readReverse(data, offset, length) : readNormal(data, offset, length);

		return new DataValue(new Variant(s));
	}

	private String readNormal(byte[] data, int offset, int length) {
		StringBuilder sb = new StringBuilder();

		for (int i = offset; i < (offset + length); i++) {
			byte b = data[i];
			if (b == 0x00) {
				break;
			}
			sb.append((char) b);
		}

		return sb.toString();
	}

	private String readReverse(byte[] data, int offset, int length) {
		StringBuilder sb = new StringBuilder();

		boolean terminated = false;
		for (int i = offset; i < (offset + length); i += 2) {
			byte[] swapped = ByteUtilities.swapBytes(data, i);
			for (int j = 0; j < swapped.length; j++) {
				if (swapped[j] == 0x0) {
					terminated = true;
					break;
				}
				sb.append((char) swapped[j]);
			}
			if (terminated) {
				break;
			}
		}

		return sb.toString();
	}

}
