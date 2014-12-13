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
package com.inductiveautomation.xopc.drivers.modbus2.protocols;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;

public class TxPacket {

	final Map<ProtocolLayer, Integer> headerPositions = new IdentityHashMap<ProtocolLayer, Integer>();
	final Map<ProtocolLayer, Integer> footerPositions = new IdentityHashMap<ProtocolLayer, Integer>();

	int start = 0;
	int end = 0;
	int currentLength = 0;
	int length = 0;

	final ApplicationData data;
	final int dataOffset;
	final ByteBuffer buffer;

	public TxPacket(ApplicationData data, int dataOffset, int length) {
		this.data = data;
		this.dataOffset = dataOffset;
		this.length = length;

		buffer = ByteBuffer.allocate(length);

		start = dataOffset;
		end = dataOffset + data.length();

		buffer.position(dataOffset);
		data.write(buffer);

		currentLength += data.length();
	}

	public int length() {
		return length;
	}

	public int currentLength() {
		return currentLength;
	}

	public ByteBuffer addHeader(ProtocolLayer layer) {
		int headerLength = layer.headerLength();

		currentLength += headerLength;

		start -= headerLength;
		headerPositions.put(layer, start);
		buffer.position(start);

		return buffer;
	}

	public ByteBuffer addFooter(ProtocolLayer layer) {
		int footerLength = layer.footerLength();

		currentLength += footerLength;

		buffer.position(end);
		footerPositions.put(layer, end);
		end += footerLength;

		return buffer;
	}

	public ByteBuffer getBuffer() {
		buffer.position(0);
		buffer.limit(buffer.capacity());
		return buffer;
	}

	public ByteBuffer dataBuffer() {
		buffer.position(dataOffset);
		buffer.limit(dataOffset + data.length());
		return buffer;
	}

	public ByteBuffer headerBufferForLayer(ProtocolLayer layer) {
		buffer.position(headerPositions.get(layer));
		buffer.limit(buffer.position() + layer.headerLength());
		return buffer;
	}

	public ByteBuffer bodyBufferForLayer(ProtocolLayer layer) {
		buffer.position(headerPositions.get(layer) + layer.headerLength());
		buffer.limit(footerPositions.get(layer));
		return buffer;
	}

	public ByteBuffer footerBufferForLayer(ProtocolLayer layer) {
		buffer.position(footerPositions.get(layer));
		buffer.limit(buffer.position() + layer.footerLength());
		return buffer;
	}
}
