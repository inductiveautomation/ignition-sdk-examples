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
package com.inductiveautomation.xopc.drivers.modbus2;

import java.sql.SQLException;
import java.util.List;

import com.google.common.collect.Lists;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.gateway.localdb.DDLSchemaFeature;
import com.inductiveautomation.ignition.gateway.localdb.SchemaFeature;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.xopc.driver.api.configuration.DriverManager;
import com.inductiveautomation.xopc.driver.api.configuration.DriverType;
import com.inductiveautomation.xopc.driver.common.AbstractDriverModuleHook;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.ModbusRtuDriverType;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.ModbusRtuOverTcpDriverType;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.ModbusTcpDriverType;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.legacy.LegacyModbusRtuConverter;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.legacy.LegacyModbusRtuOverTcpConverter;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.legacy.LegacyModbusTcpConverter;
import org.apache.log4j.Logger;

public class ModuleHook extends AbstractDriverModuleHook {

	private static final String MODBUS_MODULE_ID = "modbusdriver2";
	private static final String SERIAL_SUPPORT_MODULE_ID = "com.inductiveautomation.serial-support-gateway";

	private static final List<DriverType> DRIVER_TYPES = Lists.newArrayList();

	static {
		DRIVER_TYPES.add(new ModbusTcpDriverType());
		DRIVER_TYPES.add(new ModbusRtuOverTcpDriverType());
	}

	@Override
	public void setup(GatewayContext context) {
		BundleUtil.get().addBundle("Modbus", getClass(), "Modbus");

		super.setup(context);
	}

	@Override
	public void shutdown() {
		BundleUtil.get().removeBundle("Modbus");

		super.shutdown();
	}

	@Override
	public void serviceReady(Class<?> serviceClass) {
		super.serviceReady(serviceClass);

		if (serviceClass == DriverManager.class) {
			// Ok, the DriverTypes have been registered, let's apply our schema hackage...
			applySchemaHacks();
		}
	}

	/**
	 * In version 1.6.0 the AddressMap column used the default length for a {@link com.inductiveautomation.ignition.gateway.localdb.persistence.StringField} of 4096, which it turns
	 * out isn't long enough for some people's address maps (~115 rows or greater)... so modify it to be backed by a
	 * LONGVARCHAR column (16MB!!).
	 */
	private void applySchemaHacks() {
		try {
			List<SchemaFeature> features = Lists.<SchemaFeature>newArrayList(
					new DDLSchemaFeature(
							"modbus-tcp-property-length-extension",
							"ALTER TABLE ModbusTcpDriverSettings ALTER AddressMap SET DATA TYPE LONGVARCHAR"));

			getContext().getSchemaUpdater().updateSchema(MODBUS_MODULE_ID, features);
		} catch (SQLException e) {
			Logger.getLogger(getClass()).warn("Unable to apply AddressMap schema update.", e);
		}

		try {
			List<SchemaFeature> features = Lists.<SchemaFeature>newArrayList(
					new DDLSchemaFeature(
							"modbus-rtu-property-length-extension",
							"ALTER TABLE ModbusRtuDriverSettings ALTER AddressMap SET DATA TYPE LONGVARCHAR"));

			getContext().getSchemaUpdater().updateSchema(MODBUS_MODULE_ID, features);
		} catch (SQLException e) {
			// Almost certainly because the table doesn't exist, which will happen if they've never had a
			// {@link ModbusRtuDriverType} load, so only log on debug.
			Logger.getLogger(getClass()).debug("Unable to apply AddressMap schema update.", e);
		}
	}

	@Override
	protected List<DriverType> getDriverTypes() {
		List<DriverType> types = Lists.newArrayList(DRIVER_TYPES);

		if (getContext() != null && getContext().getModuleManager().getModule(SERIAL_SUPPORT_MODULE_ID) != null) {
			// TODO add ModbusRtuDriverType.
			types.add(new ModbusRtuDriverType());
		}

		return types;
	}

	@Override
	protected int getExpectedAPIVersion() {
		return 4;
	}

	@Override
	protected void runLegacyConversions(GatewayContext context) {
		new LegacyModbusTcpConverter(context).convert();
		new LegacyModbusRtuOverTcpConverter(context).convert();
		new LegacyModbusRtuConverter(context).convert();
	}

}
