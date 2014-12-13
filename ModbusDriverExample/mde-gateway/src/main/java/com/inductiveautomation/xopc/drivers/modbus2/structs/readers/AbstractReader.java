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
package com.inductiveautomation.xopc.drivers.modbus2.structs.readers;

import java.nio.ByteBuffer;

import com.inductiveautomation.ignition.common.util.SoftReferenceBag;
import com.inductiveautomation.xopc.drivers.modbus2.structs.ExceptionCode;
import com.inductiveautomation.xopc.drivers.modbus2.structs.ExceptionResponse;
import com.inductiveautomation.xopc.drivers.modbus2.structs.FunctionCode;

public abstract class AbstractReader<T> implements Reader<T> {

	private static final TrackerPool pool = new TrackerPool();

	@Override
	public T read(ByteBuffer buffer) throws ReadException, ModbusResponseException {
		return read(buffer, buffer.position());
	}

	@Override
	public T read(ByteBuffer buffer, int position) throws ReadException, ModbusResponseException {
		FieldTracker tracker = pool.take();

		buffer.position(position);

		try {
			byte functionCodeByte = buffer.get();

			if ((functionCodeByte & 0x80) == 0x80 || (functionCodeByte & 0x90) == 0x90) {
				byte exceptionCodeByte = buffer.get();
				ExceptionCode exceptionCode = ExceptionCode.fromByte(exceptionCodeByte);
				ExceptionResponse response = new ExceptionResponse(functionCodeByte, exceptionCode);

				throw new ModbusResponseException(response);
			}

			FunctionCode functionCode = FunctionCode.fromByte(functionCodeByte);

			return readFields(buffer, tracker, functionCode);
		} catch (ModbusResponseException e) {
			throw e;
		} catch (Exception e) {
			throw new ReadException(tracker.getFieldName(), buffer.array(), e);
		} finally {
			pool.put(tracker);
		}
	}

	protected abstract T readFields(ByteBuffer buffer, FieldTracker tracker, FunctionCode functionCode);

	protected static class FieldTracker {

		private String fieldName;

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}

	}

	private static class TrackerPool {

		private SoftReferenceBag<FieldTracker> bag = new SoftReferenceBag<FieldTracker>();

		public FieldTracker take() {
			FieldTracker tracker = null;

			synchronized (bag) {
				tracker = bag.take();
			}

			if (tracker == null) {
				tracker = new FieldTracker();
			}

			return tracker;
		}

		public void put(FieldTracker tracker) {
			synchronized (bag) {
				bag.put(tracker);
			}
		}

	}

}
