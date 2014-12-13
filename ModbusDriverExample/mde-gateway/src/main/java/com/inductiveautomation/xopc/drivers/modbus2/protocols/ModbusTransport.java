package com.inductiveautomation.xopc.drivers.modbus2.protocols;

import com.inductiveautomation.xopc.drivers.modbus2.util.Sequence;
import com.inductiveautomation.xopc.drivers.modbus2.util.Source;

public interface ModbusTransport extends Sequence<TxPacket> {

	/**
	 * @param applicationData
	 *            The {@link com.inductiveautomation.xopc.drivers.modbus2.util.Source} of the {@link com.inductiveautomation.xopc.drivers.modbus2.protocols.ApplicationData}.
	 */
	public void setApplicationDataSource(Source<ApplicationData> applicationData);

	/**
	 * @return The transaction key used in the current {@link com.inductiveautomation.xopc.drivers.modbus2.protocols.TxPacket}.
	 */
	public Object getKey();

	/**
	 * @return The {@link com.inductiveautomation.xopc.drivers.modbus2.protocols.ProtocolStack} used by this transport.
	 */
	public ProtocolStack getProtocolStack();

}
