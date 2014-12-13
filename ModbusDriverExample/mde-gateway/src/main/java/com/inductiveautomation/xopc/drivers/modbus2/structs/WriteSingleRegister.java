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

public class WriteSingleRegister {

	/**
	 * <p>
	 * This function code is used to write a single holding register in a remote device.
	 * </p>
	 * 
	 * <p>
	 * The Request PDU specifies the address of the register to be written. Registers are addressed
	 * starting at zero. Therefore register numbered 1 is addressed as 0.
	 * </p>
	 * 
	 * <pre>
	 * <b>Function Code</b> 	1 Byte		0x06
	 * <b>Register Address</b>	2 Bytes		0x0000 to 0xFFFF
	 * <b>Register Value</b> 	2 Bytes		0x0000 to 0xFFFF
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Request implements HasLength {

		private static final FunctionCode FUNCTION_CODE = FunctionCode.WriteSingleRegister;

		private final short registerAddress;
		private final short registerValue;

		public Request(short registerAddress, short registerValue) {
			this.registerAddress = registerAddress;
			this.registerValue = registerValue;
		}

		public FunctionCode getFunctionCode() {
			return FUNCTION_CODE;
		}

		public short getRegisterAddress() {
			return registerAddress;
		}

		public short getRegisterValue() {
			return registerValue;
		}

		@Override
		public int length() {
			return 5;
		}

	}

	/**
	 * <p>
	 * The normal response is an echo of the request, returned after the register contents have been
	 * written.
	 * </p>
	 * 
	 * <pre>
	 * <b>Function Code</b> 	1 Byte		0x06
	 * <b>Register Address</b>	2 Bytes		0x0000 to 0xFFFF
	 * <b>Register Value</b> 	2 Bytes		0x0000 to 0xFFFF
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Response {

		private final FunctionCode functionCode;
		private final short registerAddress;
		private final short registerValue;

		public Response(FunctionCode functionCode, short registerAddress, short registerValue) {
			this.functionCode = functionCode;
			this.registerAddress = registerAddress;
			this.registerValue = registerValue;
		}

		public FunctionCode getFunctionCode() {
			return functionCode;
		}

		public short getRegisterAddress() {
			return registerAddress;
		}

		public short getRegisterValue() {
			return registerValue;
		}

	}

}
