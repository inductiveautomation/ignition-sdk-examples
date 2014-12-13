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
package com.inductiveautomation.xopc.drivers.modbus2.structs;

import java.util.HashMap;
import java.util.Map;

public enum FunctionCode {

	/**
	 * This function code is used to read from 1 to 2000 contiguous status of coils in a remote
	 * device.
	 */
	ReadCoils(0x01),

	/**
	 * This function code is used to read from 1 to 2000 contiguous status of discrete inputs in a
	 * remote device.
	 */
	ReadDiscreteInputs(0x02),

	/**
	 * This function code is used to read the contents of a contiguous block of holding registers in
	 * a remote device.
	 */
	ReadHoldingRegisters(0x03),

	/**
	 * This function code is used to read from 1 to 125 contiguous input registers in a remote
	 * device.
	 */
	ReadInputRegisters(0x04),

	/**
	 * This function code is used to write a single output to either ON or OFF in a remote device.
	 */
	WriteSingleCoil(0x05),

	/**
	 * This function code is used to write a single holding register in a remote device.
	 */
	WriteSingleRegister(0x06),

	/**
	 * This function code is used to force each coil in a sequence of coils to either ON or OFF in a
	 * remote device.
	 */
	WriteMultipleCoils(0x0F),

	/**
	 * This function code is used to write a block of contiguous registers (1 to 123 registers) in a
	 * remote device.
	 */
	WriteMultipleRegisters(0x10),

	/**
	 * This function code is used to modify the contents of a specified holding register using a
	 * combination of an AND mask, an OR mask, and the register's current contents.
	 */
	MaskWriteRegister(0x16),

	/**
	 * This function code performs a combination of one read operation and one write operation in a
	 * single MODBUS transaction.
	 */
	ReadWriteMultipleRegisters(0x17),

	/**
	 * This function code allows reading the identification and additional information relative to
	 * the physical and functional description of a remote device, only.
	 */
	ReadDeviceIdentification(0x2B);

	private final byte functionCode;

	/**
	 * @param functionCode
	 *            The byte used to identify this function code in a request PDU.
	 */
	private FunctionCode(int functionCode) {
		this.functionCode = (byte) functionCode;
	}

	public byte byteValue() {
		return functionCode;
	}

	private static final Map<Byte, FunctionCode> cache = new HashMap<Byte, FunctionCode>();
	static {
		for (FunctionCode f : FunctionCode.values()) {
			cache.put(f.byteValue(), f);
		}
	}

	public static FunctionCode fromByte(byte b) {
		return cache.get(b);
	}

}
