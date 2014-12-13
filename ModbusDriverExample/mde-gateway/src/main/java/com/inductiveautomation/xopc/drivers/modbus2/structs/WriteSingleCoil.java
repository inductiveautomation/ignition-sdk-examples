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

public class WriteSingleCoil {

	/**
	 * <p>
	 * This function code is used to write a single output to either ON or OFF in a remote device.
	 * </p>
	 * 
	 * <p>
	 * The requested ON/OFF state is specified by a constant in the request data field. A value of
	 * FF 00 hex requests the output to be ON. A value of 00 00 requests it to be OFF. All other
	 * values are illegal and will not affect the output.
	 * </p>
	 * 
	 * <p>
	 * The Request PDU specifies the address of the coil to be forced. Coils are addressed starting
	 * at zero. Therefore coil numbered 1 is addressed as 0. The requested ON/OFF state is specified
	 * by a constant in the Coil Value field. A value of 0XFF00 requests the coil to be ON. A value
	 * of 0X0000 requests the coil to be off. All other values are illegal and will not affect the
	 * coil.
	 * </p>
	 * 
	 * <pre>
	 * <b>Function Code</b> 	1 Byte		0x05
	 * <b>Output Address</b>	2 Bytes		0x0000 to 0xFFFF
	 * <b>Output Value</b>  	2 Bytes		0x0000 or 0xFF00
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Request implements HasLength {

		private static final FunctionCode FUNCTION_CODE = FunctionCode.WriteSingleCoil;

		private final short outputAddress;
		private final short outputValue;

		public Request(short outputAddress, short outputValue) {
			this.outputAddress = outputAddress;
			this.outputValue = outputValue;
		}

		public FunctionCode getFunctionCode() {
			return FUNCTION_CODE;
		}

		public short getOutputAddress() {
			return outputAddress;
		}

		public short getOutputValue() {
			return outputValue;
		}

		@Override
		public int length() {
			return 5;
		}

	}

	/**
	 * <p>
	 * The normal response is an echo of the request, returned after the coil state has been
	 * written.
	 * </p>
	 * 
	 * <pre>
	 * <b>Function Code</b> 	1 Byte		0x05
	 * <b>Output Address</b>	2 Bytes		0x0000 to 0xFFFF
	 * <b>Output Value</b>  	2 Bytes		0x0000 or 0xFF00
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Response {

		private final FunctionCode functionCode;
		private final short outputAddress;
		private final short outputValue;

		public Response(FunctionCode functionCode, short outputAddress, short outputValue) {
			this.functionCode = functionCode;
			this.outputAddress = outputAddress;
			this.outputValue = outputValue;
		}

		public FunctionCode getFunctionCode() {
			return functionCode;
		}

		public short getOutputAddress() {
			return outputAddress;
		}

		public short getOutputValue() {
			return outputValue;
		}

	}

}
