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

public class WriteMultipleRegisters {

	/**
	 * <p>
	 * This function code is used to write a block of contiguous registers (1 to 123 registers) in a
	 * remote device.
	 * </p>
	 * 
	 * <p>
	 * The requested written values are specified in the request data field. Data is packed as two
	 * bytes per register.
	 * </p>
	 * 
	 * <pre>
	 * <b>Function Code</b> 		1 Byte			0x10
	 * <b>Starting Address</b>		2 Bytes			0x0000 to 0xFFFF
	 * <b>Register Quantity</b>		2 Bytes			0x0001 to 0x007B
	 * <b>Byte Count</b>    		1 Byte			2 x N*
	 * <b>Register Values</b>		N* x 2 Bytes
	 * 
	 * N* = Register Quantity
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Request implements HasLength {

		private static final FunctionCode FUNCTION_CODE = FunctionCode.WriteMultipleRegisters;

		private final short startAddress;
		private final short quantity;
		private final byte byteCount;
		private final byte[] values;

		public Request(short startAddress, short quantity, byte byteCount, byte[] values) {
			this.startAddress = startAddress;
			this.quantity = quantity;
			this.byteCount = byteCount;
			this.values = values;
		}

		public FunctionCode getFunctionCode() {
			return FUNCTION_CODE;
		}

		public short getStartAddress() {
			return startAddress;
		}

		public short getQuantity() {
			return quantity;
		}

		public byte getByteCount() {
			return byteCount;
		}

		public byte[] getValues() {
			return values;
		}

		@Override
		public int length() {
			return 1 + 2 + 2 + 1 + values.length;
		}

	}

	/**
	 * <p>
	 * The normal response returns the function code, starting address, and quantity of registers
	 * written.
	 * </p>
	 * 
	 * <pre>
	 * <b>Function Code</b> 		1 Byte			0x10
	 * <b>Starting Address</b>		2 Bytes			0x0000 to 0xFFFF
	 * <b>Register Quantity</b>		2 Bytes			0x0001 to 0x007B
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Response {

		private final FunctionCode functionCode;
		private final short startAddress;
		private final short quantity;

		public Response(FunctionCode functionCode, short startAddress, short quantity) {
			this.functionCode = functionCode;
			this.startAddress = startAddress;
			this.quantity = quantity;
		}

		public FunctionCode getFunctionCode() {
			return functionCode;
		}

		public short getStartAddress() {
			return startAddress;
		}

		public short getQuantity() {
			return quantity;
		}

	}

}
