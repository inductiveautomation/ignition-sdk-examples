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
package com.inductiveautomation.xopc.drivers.modbus2.configuration.map;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ForwardingMap;

public class MutableModbusAddressMap extends ForwardingMap<DesignatorRange, ModbusRange> implements
		ModbusAddressMap {

	private Map<DesignatorRange, ModbusRange> addressMap = new LinkedHashMap<DesignatorRange, ModbusRange>();
	private int designatorRadix;

	public MutableModbusAddressMap() {
		this(10);
	}

	public MutableModbusAddressMap(int designatorRadix) {
		this.designatorRadix = designatorRadix;
	}

	public void setDesignatorRadix(int designatorRadix) {
		this.designatorRadix = designatorRadix;
	}

	@Override
	public int getDesignatorRadix() {
		return designatorRadix;
	}

	@Override
	protected Map<DesignatorRange, ModbusRange> delegate() {
		return addressMap;
	}

	public String toParseableString() {
		return ModbusStringParser.toParseableString(this);
	}

	public static MutableModbusAddressMap fromParseableString(String s) {
		return ModbusStringParser.fromParseableString(s);
	}

	public void toCsv(OutputStream outputStream) throws Exception {
		ModbusCsvParser.toCsv(this, outputStream);
	}

	public static MutableModbusAddressMap fromCsv(InputStream inputStream) throws Exception {
		return ModbusCsvParser.fromCsv(inputStream);
	}

	public static class MutableDesignatorRange implements DesignatorRange {

		private String designator;
		private String start;
		private String end;
		private boolean step;

		public MutableDesignatorRange() {
			designator = "";
			start = "";
			end = "";
			step=false;
		}

		public MutableDesignatorRange(DesignatorRange range) {
			designator = range.getDesignator();
			start = range.getStart();
			end = range.getEnd();
			step=range.getStep();
		}

		public void setDesignator(String designator) {
			this.designator = designator;
		}

		public void setStart(String start) {
			this.start = start;
		}

		public void setEnd(String end) {
			this.end = end;
		}
		
		public void setStep(boolean step) {
			this.step = step;
		}

		@Override
		public String getDesignator() {
			return designator;
		}

		@Override
		public String getStart() {
			return start;
		}

		@Override
		public String getEnd() {
			return end;
		}
		
		@Override
		public boolean getStep() {
			return step;
		}
	}

	public static class MutableModbusRange implements ModbusRange {
		private String unitID;
		private AddressType modbusAddressType;
		private String start;

		public MutableModbusRange() {
			unitID = "0";
			modbusAddressType = AddressType.HoldingRegister;
			start = "";
		}

		public MutableModbusRange(ModbusRange range) {
			unitID = range.getUnitID();
			modbusAddressType = range.getModbusAddressType();
			start = range.getStart();
		}
		
		public void setUnitID(String unitID) {
			this.unitID = unitID;
		}

		public void setModbusAddressType(AddressType modbusAddressType) {
			this.modbusAddressType = modbusAddressType;
		}

		public void setStart(String start) {
			this.start = start;
		}
		
		@Override
		public String getUnitID() {
			return unitID;
		}		

		@Override
		public AddressType getModbusAddressType() {
			return modbusAddressType;
		}

		@Override
		public String getStart() {
			return start;
		}
	}

}
