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

import java.util.Iterator;

public class ModbusStringParser {

	private static final String entrySeparatorEscaped = "\\|";
	private static final String entrySeparator = "|";
	private static final String varSeparator = ",";

	public static String toParseableString(ModbusAddressMap addressMap) {
		StringBuilder sb = new StringBuilder();

		sb.append(addressMap.getDesignatorRadix());

		Iterator<DesignatorRange> drIter = addressMap.keySet().iterator();

		while (drIter.hasNext()) {
			DesignatorRange dr = drIter.next();
			ModbusRange mr = addressMap.get(dr);

			sb.append(String
					.format("%s%s,%s,%s,%s,%s,%s,%s", entrySeparator, dr.getDesignator(), dr.getStart(), dr
							.getEnd(), dr.getStep(), mr.getUnitID(), mr.getModbusAddressType(), mr.getStart()));

		}

		return sb.toString();
	}

	public static MutableModbusAddressMap fromParseableString(String s) {
		if (s != null) {
			MutableModbusAddressMap addressMap = new MutableModbusAddressMap();

			String[] entries = s.split(entrySeparatorEscaped);

			for (int i = 0; i < entries.length; i++) {
				String entry = entries[i];

				if (i == 0) {
					try {
						int radix = Integer.parseInt(entry);
						addressMap.setDesignatorRadix(radix);
					} catch (Exception e) {
					}
				} else {
					String[] vars = entry.split(varSeparator);

					if (vars.length == 7) {
						MutableModbusAddressMap.MutableDesignatorRange dr = new MutableModbusAddressMap.MutableDesignatorRange();
						dr.setDesignator(vars[0]);
						dr.setStart(vars[1]);
						dr.setEnd(vars[2]);
						dr.setStep(Boolean.parseBoolean(vars[3]));

						MutableModbusAddressMap.MutableModbusRange mr = new MutableModbusAddressMap.MutableModbusRange();
						mr.setUnitID(vars[4]);
						
						AddressType modbusType = AddressType.fromString(vars[5]);
						if (modbusType == null) {
							continue;
						} else {
							mr.setModbusAddressType(modbusType);
						}

						mr.setStart(vars[6]);

						addressMap.put(dr, mr);					
					} else if(vars.length == 6) {
						//Here to handle previous version without Unit ID
						MutableModbusAddressMap.MutableDesignatorRange dr = new MutableModbusAddressMap.MutableDesignatorRange();
						dr.setDesignator(vars[0]);
						dr.setStart(vars[1]);
						dr.setEnd(vars[2]);
						dr.setStep(Boolean.parseBoolean(vars[3]));

						MutableModbusAddressMap.MutableModbusRange mr = new MutableModbusAddressMap.MutableModbusRange();
						mr.setUnitID("0");
						
						AddressType modbusType = AddressType.fromString(vars[4]);
						if (modbusType == null) {
							continue;
						} else {
							mr.setModbusAddressType(modbusType);
						}

						mr.setStart(vars[5]);

						addressMap.put(dr, mr);						
					}
				}
			}

			return addressMap;
		}

		return null;
	}
}
