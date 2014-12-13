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

public class ReadHoldingRegisters {

	/**
	 * This function code is used to read the contents of a contiguous block of holding registers in
	 * a remote device. The Request PDU specifies the starting register address and the number of
	 * registers. In the PDU Registers are addressed starting at zero. Therefore registers numbered
	 * 1-16 are addressed as 0-15. <br>
	 * <br>
	 * 
	 * <pre>
	 * <b>Function Code</b> 		1 Byte		0x03
	 * <b>Starting Address</b>  	2 Bytes		0x0000 - 0xFFFF
	 * <b>Quantity of Registers</b>	2 Bytes		1 - 125 (0x7D)
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Request implements ReadRequest {

		private static final FunctionCode FUNCTION_CODE = FunctionCode.ReadHoldingRegisters;

		private final short startAddress;
		private final short quantity;

		public Request(short startAddress, short quantity) {
			this.startAddress = startAddress;
			this.quantity = quantity;
		}

		@Override
		public FunctionCode getFunctionCode() {
			return FUNCTION_CODE;
		}

		@Override
		public short getStartAddress() {
			return startAddress;
		}

		@Override
		public short getQuantity() {
			return quantity;
		}

		@Override
		public int length() {
			return 5;
		}

	}

	/**
	 * The register data in the response message are packed as two bytes per register, with the
	 * binary contents right justified within each byte. For each register, the first byte contains
	 * the high order bits and the second contains the low order bits. <br>
	 * <br>
	 * 
	 * <pre>
	 * <b>Function Code</b>		1 Byte			0x03
	 * <b>Byte Count</b>		1 Byte			2 x N*
	 * <b>Register Values</b> 	N* x 2 Bytes	
	 * 
	 * *N = Quantity of Registers
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Response {

		private final FunctionCode functionCode;
		private final byte byteCount;
		private final byte[] registerValues;

		public Response(FunctionCode functionCode, byte byteCount, byte[] registerValues) {
			this.functionCode = functionCode;
			this.byteCount = byteCount;
			this.registerValues = registerValues;
		}

		public FunctionCode getFunctionCode() {
			return functionCode;
		}

		public byte getByteCount() {
			return byteCount;
		}

		public byte[] getRegisterValues() {
			return registerValues;
		}

	}
}
