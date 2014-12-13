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

import com.inductiveautomation.opcua.types.DataType;

public enum ModbusDataType {

	Boolean(DataType.Boolean),
	Int16(DataType.Int16),
	UInt16(DataType.UInt16),
	Int32(DataType.Int32),
	UInt32(DataType.UInt32),
	Int64(DataType.Int64),
	UInt64(DataType.UInt64),
	Float(DataType.Float),
	Double(DataType.Double),
	BCD16(DataType.UInt16),
	BCD32(DataType.UInt32),
	String(DataType.String);

	private final DataType dataType;

	private ModbusDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return The UA {@link com.inductiveautomation.opcua.types.DataType} that backs this {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusDataType}. Note that this is to
	 *         be ignored when the {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress} is addressing a bit on a register.
	 * 
	 *         The UA {@link com.inductiveautomation.opcua.types.DataType} for a given {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress} should be retrieved using
	 *         {@link com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress#getUADataType()} as it can take this information into account.
	 */
	DataType getUADataType() {
		return dataType;
	}

}
