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

public class ReadCoils {

	/**
	 * This function code is used to read from 1 to 2000 contiguous status of coils in a remote
	 * device. <br>
	 * <br>
	 * The Request PDU specifies the starting address, i.e. the address of the first coil specified,
	 * and the number of coils. In the PDU Coils are addressed starting at zero. Therefore coils
	 * numbered 1-16 are addressed as 0-15. <br>
	 * <br>
	 * 
	 * <pre>
	 * <b>Function Code</b>		1 Byte		0x01
	 * <b>Starting Address</b>	2 Bytes 	0x0000 - 0xFFFF
	 * <b>Quantity of Coils</b>	2 Bytes 	1 - 2000 (0x7D0)
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Request implements ReadRequest {

		private static final FunctionCode FUNCTION_CODE = FunctionCode.ReadCoils;

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
	 * The coils in the response message are packed as one coil per bit of the data field. Status is
	 * indicated as 1= ON and 0= OFF. The LSB of the first data byte contains the output addressed
	 * in the query. The other coils follow toward the high order end of this byte, and from low
	 * order to high order in subsequent bytes. <br>
	 * <br>
	 * If the returned output quantity is not a multiple of eight, the remaining bits in the final
	 * data byte will be padded with zeros (toward the high order end of the byte). The Byte Count
	 * field specifies the quantity of complete bytes of data. <br>
	 * <br>
	 * 
	 * <pre>
	 * <b>Function Code</b>	1 Byte		0x01
	 * <b>Byte Count</b>	1 Byte		N*
	 * <b>Coil Status</b>	n Byte(s)	n = N or N+1
	 * 
	 * *N = Quantity of Outputs / 8, if the remainder is different of 0 then N = N+1
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Response {

		private final FunctionCode functionCode;
		private final byte byteCount;
		private final byte[] coilStatus;

		public Response(FunctionCode functionCode, byte byteCount, byte[] coilStatus) {
			this.functionCode = functionCode;
			this.byteCount = byteCount;
			this.coilStatus = coilStatus;
		}

		public FunctionCode getFunctionCode() {
			return functionCode;
		}

		public byte getByteCount() {
			return byteCount;
		}

		public byte[] getCoilStatus() {
			return coilStatus;
		}

	}
}
