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

public enum ExceptionCode {

	/**
	 * The function code received in the query is not an allowable action for the server (or slave).
	 * This may be because the function code is only applicable to newer devices, and was not
	 * implemented in the unit selected. It could also indicate that the server (or slave) is in the
	 * wrong state to process a request of this type, for example because it is not configured and
	 * is being asked to return register values.
	 */
	IllegalFunction(0x01),

	/**
	 * The data address received in the query is not an allowable address for the server (or slave).
	 * More specifically, the combination of reference number and transfer length is invalid. For a
	 * controller with 100 registers, the PDU addresses the first register as 0, and the last one as
	 * 99. If a request is submitted with a starting register address of 96 and a quantity of
	 * registers of 4, then this request will successfully operate (address-wise at least) on
	 * registers 96, 97, 98, 99. If a request is submitted with a starting register address of 96
	 * and a quantity of registers of 5, then this request will fail with Exception Code 0x02
	 * 'Illegal Data Address' since it attempts to operate on registers 96, 97, 98, 99 and 100, and
	 * there is no register with address 100.
	 */
	IllegalDataAddress(0x02),

	/**
	 * A value contained in the query data field is not an allowable value for server (or slave).
	 * This indicates a fault in the structure of the remainder of a complex request, such as that
	 * the implied length is incorrect. It specifically does NOT mean that a data item submitted for
	 * storage in a register has a value outside the expectation of the application program, since
	 * the MODBUS protocol is unaware of the significance of any particular value of any particular
	 * register.
	 */
	IllegalDataValue(0x03),

	/**
	 * An unrecoverable error occurred while the server (or slave) was attempting to perform the
	 * requested action.
	 */
	SlaveDeviceFailure(0x04),

	/**
	 * Specialized use in conjunction with programming commands. The server (or slave) has accepted
	 * the request and is processing it, but a long duration of time will be required to do so. This
	 * response is returned to prevent a timeout error from occurring in the client (or master). The
	 * client (or master) can next issue a Poll Program Complete message to determine if processing
	 * is completed.
	 */
	Acknowledge(0x05),

	/**
	 * Specialized use in conjunction with programming commands. The server (or slave) is engaged in
	 * processing a long'duration program command. The client (or master) should retransmit the
	 * message later when the server (or slave) is free.
	 */
	SlaveDeviceBusy(0x06),

	/**
	 * Specialized use in conjunction with function codes 20 and 21 and reference type 6, to
	 * indicate that the extended file area failed to pass a consistency check. The server (or
	 * slave) attempted to read record file, but detected a parity error in the memory. The client
	 * (or master) can retry the request, but service may be required on the server (or slave)
	 * device.
	 */
	MemoryParityError(0x08),

	/**
	 * Specialized use in conjunction with gateways, indicates that the gateway was unable to
	 * allocate an internal communication path from the input port to the output port for processing
	 * the request. Usually means that the gateway is misconfigured or overloaded.
	 */
	GatewayPathUnavailable(0x0A),

	/**
	 * Specialized use in conjunction with gateways, indicates that no response was obtained from
	 * the target device. Usually means that the device is not present on the network.
	 */
	GatewayTargetDeviceFailToRespond(0x0B);

	private final byte exceptionCode;

	/**
	 * @param exceptionCode
	 *            The byte used to identify this exception code in the response PDU.
	 */
	private ExceptionCode(int exceptionCode) {
		this.exceptionCode = (byte) exceptionCode;
	}

	public byte byteValue() {
		return exceptionCode;
	}

	private static final Map<Byte, ExceptionCode> cache = new HashMap<Byte, ExceptionCode>();
	static {
		for (ExceptionCode e : ExceptionCode.values()) {
			cache.put(e.exceptionCode, e);
		}
	}

	public static ExceptionCode fromByte(byte b) {
		return cache.get(b);
	}

	@Override
	public String toString() {
		return String.format("0x%02X (%s)", exceptionCode, super.toString());
	}

}
