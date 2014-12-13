package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings;

/**
 * Modbus configuration settings common to both the RTU and TCP drivers.
 */
public interface ModbusDriverSettings {

	int getCommunicationTimeout();

	int getMaxCoilsPerRequest();

	int getMaxHoldingRegistersPerRequest();

	int getMaxInputRegistersPerRequest();

	int getMaxDiscreteInputsPerRequest();

	boolean isReverseWordOrder();

	boolean isZeroBasedAddressing();

	boolean isSpanGaps();

	boolean isWriteMultipleRegistersRequestAllowed();

	boolean isWriteMultipleCoilsRequestAllowed();

	boolean isReadMultipleRegistersRequestAllowed();

	boolean isReadMultipleCoilsAllowed();

	boolean isReadMultipleDiscreteInputsAllowed();

	boolean isReconnectAfterConsecutiveTimeouts();

	boolean isReverseStringByteOrder();

	boolean isRightJustifyStrings();

	String getAddressMap();

	void setAddressMap(String addressMap);

}
