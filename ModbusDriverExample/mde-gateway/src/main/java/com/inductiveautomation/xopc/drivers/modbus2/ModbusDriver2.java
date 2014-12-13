package com.inductiveautomation.xopc.drivers.modbus2;

import java.nio.ByteBuffer;

import com.inductiveautomation.opcua.types.DataType;
import com.inductiveautomation.opcua.types.DataValue;
import com.inductiveautomation.opcua.types.UInt16;
import com.inductiveautomation.opcua.types.Variant;
import com.inductiveautomation.xopc.driver.api.DriverContext;
import com.inductiveautomation.xopc.driver.api.DriverIODelegate;
import com.inductiveautomation.xopc.driver.api.SocketIODelegate;
import com.inductiveautomation.xopc.driver.api.tags.StaticDriverTag;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.ModbusTcpDriverSettings;
import com.inductiveautomation.xopc.drivers.modbus2.factories.ModbusTransportFactory;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTCPTransport;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;
import com.inductiveautomation.xopc.drivers.modbus2.structs.MBAPHeader;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.MBAPHeaderReader;
import com.inductiveautomation.xopc.drivers.modbus2.structs.readers.ReadException;
import com.inductiveautomation.xopc.drivers.modbus2.util.Sequence;
import com.inductiveautomation.xopc.drivers.modbus2.util.ShortSequence;

public class ModbusDriver2 extends AbstractModbusDriver {

	private static final MBAPHeaderReader headerReader = new MBAPHeaderReader();

	private final Sequence<Short> sequence = new ShortSequence();

	private volatile SocketIODelegate ioDelegate;

	public ModbusDriver2(DriverContext driverContext, ModbusTcpDriverSettings settings) {
		super(driverContext, settings);

		String hostname = settings.getHostname();
		Integer port = settings.getPort();

		addDriverTag(new StaticDriverTag("[Diagnostics]/Hostname",
										 DataType.String,
										 new DataValue(new Variant(hostname))));

		addDriverTag(new StaticDriverTag("[Diagnostics]/Port",
										 DataType.UInt16,
										 new DataValue(new Variant(new UInt16(port)))));

		ioDelegate = new SocketIODelegate(this, hostname, port);
	}

	@Override
	protected ModbusTransportFactory getTransportFactory() {
		return new ModbusTransportFactory() {
			@Override
			public ModbusTransport get(byte unitId) {
				return new ModbusTCPTransport(sequence, unitId);
			}
		};
	}

	@Override
	protected int messageLength(ByteBuffer buffer) {
		if (buffer.limit() - buffer.position() < MBAPHeader.HEADER_LENGTH) {
			return -1;
		}

		try {
			MBAPHeader header = headerReader.read(buffer);

			// The length specified in the header is for the 1-byte unitId plus the bytes that make
			// up the response; we need to add 6 to account for the first 3 2-byte fields of the
			// header.
			return header.getLength() + 6;
		} catch (ReadException e) {
			log.warn("Error encountered while reading MBAPHeader in messageLength().", e);
		}

		return -1;
	}

	@Override
	protected Object getRequestKey(byte[] message) {
		short transactionId = -1;

		try {
			MBAPHeader header = headerReader.read(ByteBuffer.wrap(message));

			transactionId = header.getTransactionId();
		} catch (ReadException e) {
			log.warn("Error encountered while reading MBAPHeader in getRequestKey().", e);
		}

		return transactionId;
	}

	@Override
	public DriverIODelegate getIODelegate() {
		return ioDelegate;
	}

}
