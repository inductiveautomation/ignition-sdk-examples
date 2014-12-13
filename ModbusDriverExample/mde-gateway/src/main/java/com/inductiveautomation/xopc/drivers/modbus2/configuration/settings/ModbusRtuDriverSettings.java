package com.inductiveautomation.xopc.drivers.modbus2.configuration.settings;

import com.inductiveautomation.ignition.gateway.localdb.persistence.BooleanField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.Category;
import com.inductiveautomation.ignition.gateway.localdb.persistence.EnumField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IntField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.LongField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.StringField;
import com.inductiveautomation.xopc.driver.api.configuration.DeviceSettingsRecord;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.BitRate;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.DataBits;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.Handshake;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.Parity;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.serial.StopBits;
import simpleorm.dataset.SFieldFlags;

public class ModbusRtuDriverSettings extends PersistentRecord implements ModbusDriverSettings {

	public static final RecordMeta<ModbusRtuDriverSettings> META =
			new RecordMeta<ModbusRtuDriverSettings>(ModbusRtuDriverSettings.class, "ModbusRtuDriverSettings");

	public static final LongField DeviceSettingsId = new LongField(META, "DeviceSettingsId", SFieldFlags.SPRIMARY_KEY);
	public static final ReferenceField<DeviceSettingsRecord> DeviceSettings = new ReferenceField<DeviceSettingsRecord>(
			META,
			DeviceSettingsRecord.META,
			"DeviceSettings",
			DeviceSettingsId);

	/* Serial specific */
	public static final StringField SerialPort = new StringField(META, "SerialPort", SFieldFlags.SMANDATORY);

	public static final EnumField<BitRate> BR = new EnumField<BitRate>(META, "BitRate", BitRate.class);
	public static final EnumField<DataBits> DB = new EnumField<DataBits>(META, "DataBits", DataBits.class);
	public static final EnumField<Parity> P = new EnumField<Parity>(META, "Parity", Parity.class);
	public static final EnumField<StopBits> SB = new EnumField<StopBits>(META, "StopBits", StopBits.class);
	public static final EnumField<Handshake> H = new EnumField<Handshake>(META, "Handshake", Handshake.class);

	/* Connectivity */
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
	public static final Category Connectivity = new Category("ModbusRtuDriverSettings.Category.Connectivity", 1001)
			.include(SerialPort, BR, DB, P, SB, H, CommunicationTimeout);

	public static final Category Advanced = new Category("ModbusRtuDriverSettings.Category.Advanced", 1002, true)
			.include(MaxHoldingRegistersPerRequest, MaxInputRegistersPerRequest, MaxCoilsPerRequest,
					 MaxDiscreteInputsPerRequest, ReverseWordOrder, ZeroBasedAddressing, SpanGaps,
					 WriteMultipleRegistersRequestAllowed, WriteMultipleCoilsRequestAllowed,
					 ReadMultipleRegistersRequestAllowed, ReadMultipleCoilsAllowed, ReadMultipleDiscreteInputsAllowed,
					 ReconnectAfterConsecutiveTimeouts);

	public static final Category StringHandling = new Category("ModbusRtuDriverSettings.Category.StringHandling", 1003, true)
			.include(ReverseStringByteOrder, RightJustifyStrings);


	static {
		DeviceSettings.getFormMeta().setVisible(false);
		AddressMap.getFormMeta().setVisible(false);
		AddressMap.setDefault("");

		// Set some defaults...
		BR.setDefault(BitRate.BitRate_115200);
		DB.setDefault(DataBits.DataBits_8);
		P.setDefault(Parity.None);
		SB.setDefault(StopBits.StopBits_1);
		H.setDefault(Handshake.None);

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

	public String getSerialPort() {
		return getString(SerialPort);
	}

	public BitRate getBitRate() {
		return getEnum(BR);
	}

	public DataBits getDataBits() {
		return getEnum(DB);
	}

	public Parity getParity() {
		return getEnum(P);
	}

	public StopBits getStopBits() {
		return getEnum(SB);
	}

	public Handshake getHandshake() {
		return getEnum(H);
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

	@Override
	public void setAddressMap(String addressMap) {
		setString(AddressMap, addressMap);
	}

	public void setSerialPort(String serialPort) {
		setString(SerialPort, serialPort);
	}

	public void setBitRate(BitRate bitRate) {
		setEnum(BR, bitRate);
	}

	public void setDataBits(DataBits dataBits) {
		setEnum(DB, dataBits);
	}

	public void setParity(Parity parity) {
		setEnum(P, parity);
	}

	public void setStopBits(StopBits stopBits) {
		setEnum(SB, stopBits);
	}

	public void setHandshake(Handshake handshake) {
		setEnum(H, handshake);
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

}
