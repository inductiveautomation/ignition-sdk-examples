package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings;

import com.inductiveautomation.ignition.gateway.localdb.persistence.BooleanField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.Category;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IntField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.LongField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.StringField;
import com.inductiveautomation.xopc.driver.api.configuration.DeviceSettingsRecord;
import simpleorm.dataset.SFieldFlags;

public class ModbusTcpDriverSettings extends PersistentRecord implements ModbusDriverSettings {

	public static final RecordMeta<ModbusTcpDriverSettings> META =
			new RecordMeta<ModbusTcpDriverSettings>(ModbusTcpDriverSettings.class, "ModbusTcpDriverSettings");

	public static final LongField DeviceSettingsId = new LongField(META, "DeviceSettingsId", SFieldFlags.SPRIMARY_KEY);
	public static final ReferenceField<DeviceSettingsRecord> DeviceSettings = new ReferenceField<DeviceSettingsRecord>(
			META,
			DeviceSettingsRecord.META,
			"DeviceSettings",
			DeviceSettingsId);

	/* Connectivity */
	public static final StringField Hostname = new StringField(META, "Hostname", SFieldFlags.SMANDATORY, SFieldFlags.SDESCRIPTIVE);
	public static final IntField Port = new IntField(META, "Port", SFieldFlags.SMANDATORY);
	public static final IntField CommunicationTimeout = new IntField(META, "CommunicationTimeout", SFieldFlags.SMANDATORY);

	/* Advanced */
	public static final IntField MaxHoldingRegistersPerRequest = new IntField(META, "MaxHoldingRegistersPerRequest", SFieldFlags.SMANDATORY);
	public static final IntField MaxInputRegistersPerRequest = new IntField(META, "MaxInputRegistersPerRequest", SFieldFlags.SMANDATORY);
	public static final IntField MaxCoilsPerRequest = new IntField(META, "MaxCoilsPerRequest", SFieldFlags.SMANDATORY);
	public static final IntField MaxDiscreteInputsPerRequest = new IntField(META, "MaxDiscreteInputsPerRequest", SFieldFlags.SMANDATORY);

	public static final BooleanField ReverseWordOrder = new BooleanField(META, "ReverseWordOrder");
	public static final BooleanField ZeroBasedAddressing = new BooleanField(META, "ZeroBasedAddressing");
	public static final BooleanField SpanGaps = new BooleanField(META, "SpanGaps");
	public static final BooleanField WriteMultipleRegistersRequestAllowed = new BooleanField(META, "WriteMultipleRegistersRequestAllowed");
	public static final BooleanField WriteMultipleCoilsRequestAllowed = new BooleanField(META, "WriteMultipleCoilsRequestAllowed");
	public static final BooleanField ReadMultipleRegistersRequestAllowed = new BooleanField(META, "ReadMultipleRegistersRequestAllowed");
	public static final BooleanField ReadMultipleCoilsAllowed = new BooleanField(META, "ReadMultipleCoilsAllowed");
	public static final BooleanField ReadMultipleDiscreteInputsAllowed = new BooleanField(META, "ReadMultipleDiscreteInputsAllowed");
	public static final BooleanField ReconnectAfterConsecutiveTimeouts = new BooleanField(META, "ReconnectAfterConsecutiveTimeouts");

	/* String handling */
	public static final BooleanField ReverseStringByteOrder = new BooleanField(META, "ReverseStringByteOrder");
	public static final BooleanField RightJustifyStrings = new BooleanField(META, "RightJustifyStrings");

	/* Hidden fields */

	// This gets schema-hacked into a LONGVARCHAR, which defaults to 16MB, so match that size to make the field
	// validators happy when setting the value.
	public static final StringField AddressMap = new StringField(META, "AddressMap", 16 * 1024 * 1024);

	/* Categories */
	public static final Category Connectivity = new Category("ModbusTcpDriverSettings.Category.Connectivity", 1001)
			.include(Hostname, Port, CommunicationTimeout);

	public static final Category Advanced = new Category("ModbusTcpDriverSettings.Category.Advanced", 1002, true)
			.include(MaxHoldingRegistersPerRequest, MaxInputRegistersPerRequest, MaxCoilsPerRequest,
					 MaxDiscreteInputsPerRequest, ReverseWordOrder, ZeroBasedAddressing, SpanGaps,
					 WriteMultipleRegistersRequestAllowed, WriteMultipleCoilsRequestAllowed,
					 ReadMultipleRegistersRequestAllowed, ReadMultipleCoilsAllowed, ReadMultipleDiscreteInputsAllowed,
					 ReconnectAfterConsecutiveTimeouts);

	public static final Category StringHandling = new Category("ModbusTcpDriverSettings.Category.StringHandling", 1003, true)
			.include(ReverseStringByteOrder, RightJustifyStrings);

	static {
		DeviceSettings.getFormMeta().setVisible(false);
		AddressMap.getFormMeta().setVisible(false);
		AddressMap.setDefault("");

		// Set some defaults...
		Port.setDefault(502);
		CommunicationTimeout.setDefault(2000);

		MaxHoldingRegistersPerRequest.setDefault(125);
		MaxInputRegistersPerRequest.setDefault(125);
		MaxCoilsPerRequest.setDefault(2000);
		MaxDiscreteInputsPerRequest.setDefault(2000);

		ReverseWordOrder.setDefault(false);
		ZeroBasedAddressing.setDefault(true);
		SpanGaps.setDefault(true);
		WriteMultipleRegistersRequestAllowed.setDefault(true);
		WriteMultipleCoilsRequestAllowed.setDefault(true);
		ReadMultipleRegistersRequestAllowed.setDefault(true);
		ReadMultipleCoilsAllowed.setDefault(true);
		ReadMultipleDiscreteInputsAllowed.setDefault(true);
		ReconnectAfterConsecutiveTimeouts.setDefault(true);

		ReverseStringByteOrder.setDefault(false);
		RightJustifyStrings.setDefault(false);
	}

	@Override
	public RecordMeta<?> getMeta() {
		return META;
	}

	public String getHostname() {
		return getString(Hostname);
	}

	public Integer getPort() {
		return getInt(Port);
	}

	@Override
	public int getCommunicationTimeout() {
		return getInt(CommunicationTimeout);
	}

	@Override
	public int getMaxCoilsPerRequest() {
		return getInt(MaxCoilsPerRequest);
	}

	@Override
	public int getMaxHoldingRegistersPerRequest() {
		return getInt(MaxHoldingRegistersPerRequest);
	}

	@Override
	public int getMaxInputRegistersPerRequest() {
		return getInt(MaxInputRegistersPerRequest);
	}

	@Override
	public int getMaxDiscreteInputsPerRequest() {
		return getInt(MaxDiscreteInputsPerRequest);
	}

	@Override
	public boolean isReverseWordOrder() {
		return getBoolean(ReverseWordOrder);
	}

	@Override
	public boolean isZeroBasedAddressing() {
		return getBoolean(ZeroBasedAddressing);
	}

	@Override
	public boolean isSpanGaps() {
		return getBoolean(SpanGaps);
	}

	@Override
	public boolean isWriteMultipleRegistersRequestAllowed() {
		return getBoolean(WriteMultipleRegistersRequestAllowed);
	}

	@Override
	public boolean isWriteMultipleCoilsRequestAllowed() {
		return getBoolean(WriteMultipleCoilsRequestAllowed);
	}

	@Override
	public boolean isReadMultipleRegistersRequestAllowed() {
		return getBoolean(ReadMultipleRegistersRequestAllowed);
	}

	@Override
	public boolean isReadMultipleCoilsAllowed() {
		return getBoolean(ReadMultipleCoilsAllowed);
	}

	@Override
	public boolean isReadMultipleDiscreteInputsAllowed() {
		return getBoolean(ReadMultipleDiscreteInputsAllowed);
	}

	@Override
	public boolean isReconnectAfterConsecutiveTimeouts() {
		return getBoolean(ReconnectAfterConsecutiveTimeouts);
	}

	@Override
	public boolean isReverseStringByteOrder() {
		return getBoolean(ReverseStringByteOrder);
	}

	@Override
	public boolean isRightJustifyStrings() {
		return getBoolean(RightJustifyStrings);
	}

	@Override
	public String getAddressMap() {
		return getString(AddressMap);
	}

	// --- Setters ---

	public void setHostname(String hostname) {
		setString(Hostname, hostname);
	}

	public void setPort(int port) {
		setInt(Port, port);
	}

	public void setCommunicationTimeout(int communicationTimeout) {
		setInt(CommunicationTimeout, communicationTimeout);
	}

	public void setMaxHoldingRegistersPerRequest(int maxHoldingRegistersPerRequest) {
		setInt(MaxHoldingRegistersPerRequest, maxHoldingRegistersPerRequest);
	}

	public void setMaxInputRegistersPerRequest(int maxInputRegistersPerRequest) {
		setInt(MaxInputRegistersPerRequest, maxInputRegistersPerRequest);
	}

	public void setMaxCoilsPerRequest(int maxCoilsPerRequest) {
		setInt(MaxCoilsPerRequest, maxCoilsPerRequest);
	}

	public void setMaxDiscreteInputsPerRequest(int maxDiscreteInputsPerRequest) {
		setInt(MaxDiscreteInputsPerRequest, maxDiscreteInputsPerRequest);
	}

	public void setReverseWordOrder(boolean reverseWordOrder) {
		setBoolean(ReverseWordOrder, reverseWordOrder);
	}

	public void setZeroBasedAddressing(boolean zeroBasedAddressing) {
		setBoolean(ZeroBasedAddressing, zeroBasedAddressing);
	}

	public void setSpanGaps(boolean spanGaps) {
		setBoolean(SpanGaps, spanGaps);
	}

	public void setWriteMultipleRegistersRequestAllowed(boolean writeMultipleRegistersRequestAllowed) {
		setBoolean(WriteMultipleRegistersRequestAllowed, writeMultipleRegistersRequestAllowed);
	}

	public void setWriteMultipleCoilsRequestAllowed(boolean writeMultipleCoilsRequestAllowed) {
		setBoolean(WriteMultipleCoilsRequestAllowed, writeMultipleCoilsRequestAllowed);
	}

	public void setReadMultipleRegistersRequestAllowed(boolean readMultipleRegistersRequestAllowed) {
		setBoolean(ReadMultipleRegistersRequestAllowed, readMultipleRegistersRequestAllowed);
	}

	public void setReadMultipleCoilsAllowed(boolean readMultipleCoilsAllowed) {
		setBoolean(ReadMultipleCoilsAllowed, readMultipleCoilsAllowed);
	}

	public void setReadMultipleDiscreteInputsAllowed(boolean readMultipleDiscreteInputsAllowed) {
		setBoolean(ReadMultipleDiscreteInputsAllowed, readMultipleDiscreteInputsAllowed);
	}

	public void setReconnectAfterConsecutiveTimeouts(boolean reconnectAfterConsecutiveTimeouts) {
		setBoolean(ReconnectAfterConsecutiveTimeouts, reconnectAfterConsecutiveTimeouts);
	}

	public void setReverseStringByteOrder(boolean reverseStringByteOrder) {
		setBoolean(ReverseStringByteOrder, reverseStringByteOrder);
	}

	public void setRightJustifyStrings(boolean rightJustifyStrings) {
		setBoolean(RightJustifyStrings, rightJustifyStrings);
	}

	@Override
	public void setAddressMap(String addressMap) {
		setString(AddressMap, addressMap);
	}

}
