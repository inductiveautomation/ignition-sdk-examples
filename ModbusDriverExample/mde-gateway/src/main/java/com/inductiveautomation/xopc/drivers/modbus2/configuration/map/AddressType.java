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
package com.inductiveautomation.xopc.drivers.modbus2.configuration.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusDataType;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;

/**
 * This is really a combination of a {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable} and a {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusDataType}. It's a
 * left-over from the legacy Modbus driver that we need to try to maintain some backwards
 * compatibility in the configuration process.
 */
public enum AddressType implements Serializable {
	DiscreteInput("Discrete Input", "DI", ModbusTable.DiscreteInputs, ModbusDataType.Boolean),
	Coil("Coil", "C", ModbusTable.Coils, ModbusDataType.Boolean),

	InputRegister("Input Register (Int16)", "IR", ModbusTable.InputRegisters, ModbusDataType.Int16),
	InputRegisterUInt16("Input Register (UInt16)", "IRUS", ModbusTable.InputRegisters, ModbusDataType.UInt16),
	InputRegisterInt32("Input Register (Int32)", "IRI", ModbusTable.InputRegisters, ModbusDataType.Int32),
	InputRegisterUInt32("Input Register (UInt32)", "IRUI", ModbusTable.InputRegisters, ModbusDataType.UInt32),
	InputRegisterInt64("Input Register (Int64)", "IRI_64", ModbusTable.InputRegisters, ModbusDataType.Int64),
	InputRegisterUInt64("Input Register (UInt64)", "IRUI_64", ModbusTable.InputRegisters, ModbusDataType.UInt64),
	InputRegisterFloat("Input Register (Float)", "IRF", ModbusTable.InputRegisters, ModbusDataType.Float),
	InputRegisterDouble("Input Register (Double)", "IRD", ModbusTable.InputRegisters, ModbusDataType.Double),
	InputRegisterBcd("Input Register (BCD16)", "IRBCD", ModbusTable.InputRegisters, ModbusDataType.BCD16),
	InputRegisterBcd32("Input Register (BCD32)", "IRBCD_32", ModbusTable.InputRegisters, ModbusDataType.BCD32),

	HoldingRegister("Holding Register (Int16)", "HR", ModbusTable.HoldingRegisters, ModbusDataType.Int16),
	HoldingRegisterUInt16("Holding Register (UInt16)", "HRUS", ModbusTable.HoldingRegisters, ModbusDataType.UInt16),
	HoldingRegisterInt32("Holding Register (Int32)", "HRI", ModbusTable.HoldingRegisters, ModbusDataType.Int32),
	HoldingRegisterUInt32("Holding Register (UInt32)", "HRUI", ModbusTable.HoldingRegisters, ModbusDataType.UInt32),
	HoldingRegisterInt64("Holding Register (Int64)", "HRI_64", ModbusTable.HoldingRegisters, ModbusDataType.Int64),
	HoldingRegisterUInt64("Holding Register (UInt64)", "HRUI_64", ModbusTable.HoldingRegisters, ModbusDataType.UInt64),
	HoldingRegisterFloat("Holding Register (Float)", "HRF", ModbusTable.HoldingRegisters, ModbusDataType.Float),
	HoldingRegisterDouble("Holding Register (Double)", "HRD", ModbusTable.HoldingRegisters, ModbusDataType.Double),
	HoldingRegisterBcd("Holding Register (BCD16)", "HRBCD", ModbusTable.HoldingRegisters, ModbusDataType.BCD16),
	HoldingRegisterBcd32("Holding Register (BCD32)", "HRBCD_32", ModbusTable.HoldingRegisters, ModbusDataType.BCD32),

	InputRegisterString("Input Register (String)", "IRS", ModbusTable.InputRegisters, ModbusDataType.String, true),
	HoldingRegisterString("Holding Register (String)", "HRS", ModbusTable.HoldingRegisters, ModbusDataType.String, true);

	private String displayString;
	private String shortString;
	private ModbusTable table;
	private ModbusDataType type;
	private boolean hidden;

	private AddressType(String displayString, String shortString, ModbusTable table, ModbusDataType type) {
		this(displayString, shortString, table, type, false);
	}

	private AddressType(String displayString, String shortString, ModbusTable table, ModbusDataType type, boolean hidden) {
		this.displayString = displayString;
		this.shortString = shortString;
		this.table = table;
		this.type = type;
		this.hidden = hidden;
	}

	public ModbusTable getModbusTable() {
		return table;
	}

	public ModbusDataType getModbusDataType() {
		return type;
	}

	public String getDisplayString() {
		return displayString;
	}

	public String getShortString() {
		return shortString;
	}

	public boolean isHidden() {
		return hidden;
	}

	private static Map<String, AddressType> stringMap = new HashMap<String, AddressType>();

	public static AddressType fromString(String s) {
		synchronized (stringMap) {
			if (stringMap.size() == 0) {
				for (AddressType type : values()) {
					stringMap.put(type.toString().toLowerCase(), type);
				}
			}
		}

		return stringMap.get(s.toLowerCase());
	}

	/**
	 * Returns the AddressType from a short string. Case-insensitive.
	 * 
	 * @param s
	 *            The short string.
	 * 
	 * @return The AddressType represented by the given short string.
	 */
	public static AddressType fromShortString(String s) {
		for (AddressType type : values()) {
			if (type.shortString.toLowerCase().equals(s.toLowerCase())) {
				return type;
			}
		}
		return null;
	}

	public static List<AddressType> getOptions() {
		List<AddressType> options = new ArrayList<AddressType>();
		for (AddressType type : values()) {
			if (!type.isHidden()) {
				options.add(type);
			}
		}
		return options;
	}

	public static AddressType forAddress(ModbusAddress address) {
		// This is ugly...
		for (AddressType at : values()) {
			if (at.table == address.getTable() && at.type == address.getModbusDataType()) {
				return at;
			}
		}
		return null;
	}

}
