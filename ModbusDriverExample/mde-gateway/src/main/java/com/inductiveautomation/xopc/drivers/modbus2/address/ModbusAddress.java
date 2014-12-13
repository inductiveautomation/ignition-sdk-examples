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
package com.inductiveautomation.xopc.drivers.modbus2.address;

import java.io.StringReader;

import org.apache.log4j.Logger;

import com.inductiveautomation.opcua.types.DataType;
import com.inductiveautomation.xopc.drivers.modbus2.address.parser.ModbusAddressParser;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.AddressType;

public class ModbusAddress {

	/** This is fine and dandy until they change someone lowers max HR per request... */
	public static final int MAX_STRING_LENGTH = 250;

	private int stringLength = -1;
	private int unitId = 0;
	private int bit = -1;

	private int startAddress;
	private ModbusTable table;
	private ModbusDataType modbusDataType;

	private ModbusAddress(ModbusTable table, ModbusDataType modbusDataType, int startAddress) {
		this.table = table;
		this.modbusDataType = modbusDataType;
		this.startAddress = startAddress;
	}

	public ModbusTable getTable() {
		return table;
	}

	/**
	 * The {@link ModbusDataType}, together with address span, dictate how many raw addresses to
	 * read and the UA {@link com.inductiveautomation.opcua.types.DataType} backing this address (except in the case where this address
	 * specifies a bit).
	 * 
	 * @return The ModbusDataType for this address.
	 */
	public ModbusDataType getModbusDataType() {
		return modbusDataType;
	}

	public int getStartAddress() {
		return startAddress;
	}

	public int getUnitId() {
		return unitId;
	}

	public int getStringLength() {
		return stringLength;
	}

	public int getBit() {
		return bit;
	}

	/**
	 * @return The number of 16-bit "slots" this value spans. This is used to support 32-bit and
	 *         String values mapped over multiple registers.
	 */
	public int getAddressSpan() {
		switch (modbusDataType) {
			case Boolean:
				return 1;

			case BCD16:
			case Int16:
			case UInt16:
				return 1;

			case BCD32:
			case Float:
			case Int32:
			case UInt32:
				return 2;

			case Int64:
			case UInt64:
			case Double:
				return 4;

			case String:
				// String length is specified in bytes so every 2 bytes is 1 slot.
				return getStringLength() / 2;

			default:
				throw new RuntimeException(String.format("Address span for %s is not defined.", modbusDataType));
		}
	}

	/**
	 * The UA {@link com.inductiveautomation.opcua.types.DataType} of the node that will be created in the address space. For addresses
	 * with a bit specified this is always {@link com.inductiveautomation.opcua.types.DataType#Boolean}. Otherwise it's up to the UA
	 * type backing this {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress}.
	 * 
	 * @return The UA {@link com.inductiveautomation.opcua.types.DataType} of the node representing this {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress} in the
	 *         address space.
	 */
	public DataType getUADataType() {
		if (bit < 0) {
			return modbusDataType.getUADataType();
		} else {
			return DataType.Boolean;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (unitId != 0) {
			sb.append(unitId);
			sb.append(".");
		}

		sb.append(AddressType.forAddress(this));
		sb.append(startAddress);

		if (bit != -1) {
			sb.append(".");
			sb.append(bit);
		}

		if (stringLength != -1) {
			sb.append(":");
			sb.append(stringLength);
		}

		return sb.toString();
	}

	private static final Logger log = Logger.getLogger(ModbusAddress.class.getSimpleName());

	public static ModbusAddress parse(String addressString) {
		if (addressString == null) {
			return null;
		}

		ModbusAddress address = null;

		ModbusAddressParser parser = new ModbusAddressParser(new StringReader(addressString));

		try {
			java_cup.runtime.Symbol sym = parser.parse();
			address = (ModbusAddress) sym.value;
		} catch (Exception e) {
			log.debug(String.format("Error parsing address: %s", addressString), e);
		} catch (Error e) {
			log.debug(String.format("Error parsing address: %s", addressString), e);
		}

		return address;
	}

	public static ModbusAddressBuilder builder(ModbusTable table, ModbusDataType dataType, int startAddress) {
		return new ModbusAddressBuilder(table, dataType, startAddress);
	}

	public static class ModbusAddressBuilder {

		private final ModbusAddress address;

		private ModbusAddressBuilder(ModbusTable table, ModbusDataType modbusDataType, int startAddress) {
			address = new ModbusAddress(table, modbusDataType, startAddress);
		}

		public ModbusAddressBuilder setUnitId(int unitId) {
			address.unitId = unitId;
			return this;
		}

		public ModbusAddressBuilder setStringLength(int stringLength) {
			// String lengths must be in the range [2..MAX_STRING_LENGTH] and even.
			stringLength = Math.max(2, stringLength);
			stringLength = Math.min(stringLength, MAX_STRING_LENGTH);
			if (stringLength % 2 != 0) {
				stringLength++;
			}

			address.stringLength = stringLength;
			return this;
		}

		public ModbusAddressBuilder setBit(int bit) {
			address.bit = bit;
			return this;
		}

		public ModbusAddress build() {
			return address;
		}

	}

}
