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

public class MaskWriteRegister {

	/**
	 * This function code is used to modify the contents of a specified holding register using a
	 * combination of an AND mask, an OR mask, and the register's current contents. The function
	 * can be used to set or clear individual bits in the register.<br>
	 * <br>
	 * The request specifies the holding register to be written, the data to be used as the AND
	 * mask, and the data to be used as the OR mask. Registers are addressed starting at zero.
	 * Therefore registers 1-16 are addressed as 0-15.<br>
	 * <br>
	 * The function's algorithm is:
	 * 
	 * <pre>
	 * Result = (Current Contents AND And_Mask) OR (Or_Mask AND (NOT And_Mask))
	 * </pre>
	 * 
	 * Note:
	 * <ul>
	 * <li>If the Or_Mask value is zero, the result is simply the logical ANDing of the current
	 * contents and And_Mask. If the And_Mask value is zero, the result is equal to the Or_Mask
	 * value.</li>
	 * <li>The contents of the register can be read with the Read Holding Registers function
	 * (function code 03). They could, however, be changed subsequently as the controller scans its
	 * user logic program.</li>
	 * </ul>
	 * 
	 * <pre>
	 * <b>Function Code</b>	    	1 Byte		0x16
	 * <b>Reference Address</b>		2 Bytes		0x0000 to 0xFFFF
	 * <b>And Mask</b>	    		2 Bytes		0x0000 to 0xFFFF
	 * <b>Or Mask</b>	    		2 Bytes		0x0000 to 0xFFFF
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Request implements HasLength {

		private static final FunctionCode FUNCTION_CODE = FunctionCode.MaskWriteRegister;

		private final short referenceAddress;
		private final short andMask;
		private final short orMask;

		public Request(short referenceAddress, short andMask, short orMask) {
			this.referenceAddress = referenceAddress;
			this.andMask = andMask;
			this.orMask = orMask;
		}

		public FunctionCode getFunctionCode() {
			return FUNCTION_CODE;
		}

		public short getReferenceAddress() {
			return referenceAddress;
		}

		public short getAndMask() {
			return andMask;
		}

		public short getOrMask() {
			return orMask;
		}

		@Override
		public int length() {
			return 7;
		}

	}

	/**
	 * The normal response is an echo of the request. The response is returned after the register
	 * has been written.
	 * 
	 * <pre>
	 * <b>Function Code</b>			1 Byte		0x16
	 * <b>Reference Address</b>		2 Bytes		0x0000 to 0xFFFF
	 * <b>And Mask</b>	    		2 Bytes		0x0000 to 0xFFFF
	 * <b>Or Mask</b>	    		2 Bytes		0x0000 to 0xFFFF
	 * </pre>
	 * 
	 * @author Kevin Herron (kevin@inductiveautomation.com)
	 */
	public static class Response {

		private final FunctionCode functionCode;
		private final short referenceAddress;
		private final short andMask;
		private final short orMask;

		public Response(FunctionCode functionCode, short referenceAddress, short andMask, short orMask) {
			this.functionCode = functionCode;
			this.referenceAddress = referenceAddress;
			this.andMask = andMask;
			this.orMask = orMask;
		}

		public FunctionCode getFunctionCode() {
			return functionCode;
		}

		public short getReferenceAddress() {
			return referenceAddress;
		}

		public short getAndMask() {
			return andMask;
		}

		public short getOrMask() {
			return orMask;
		}

	}

}
