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
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusRTUTransport;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;
import com.inductiveautomation.xopc.drivers.modbus2.structs.FunctionCode;

public class ModbusOverTCPDriver extends AbstractModbusDriver {

	private volatile SocketIODelegate ioDelegate;

	public ModbusOverTCPDriver(DriverContext driverContext, ModbusTcpDriverSettings settings) {
		super(driverContext, settings);

		String hostname = settings.getHostname();
		Integer port = settings.getPort();

		addDriverTag(new StaticDriverTag("[Diagnostics]/Hostname", DataType.String,
										 new DataValue(new Variant(hostname))));
		addDriverTag(new StaticDriverTag("[Diagnostics]/Port", DataType.UInt16,
										 new DataValue(new Variant(new UInt16(port)))));

		ioDelegate = new SocketIODelegate(this, hostname, port);
	}

	@Override
	protected ModbusTransportFactory getTransportFactory() {
		return new ModbusTransportFactory() {
			@Override
			public ModbusTransport get(byte unitId) {
				return new ModbusRTUTransport(unitId);
			}
		};
	}

	@Override
	protected int messageLength(ByteBuffer buffer) {
		// SlaveId (1), FunctionCode (1), Body (2), CRC (2)
		if (buffer.limit() - buffer.position() < 6) {
			return -1;
		}

		int length = 0;

		@SuppressWarnings("unused")
		byte slaveId = buffer.get();
		length++;

		FunctionCode functionCode = FunctionCode.fromByte(buffer.get());
		length++;

		switch (functionCode) {
			case ReadHoldingRegisters:
			case ReadInputRegisters:
			case ReadCoils:
			case ReadDiscreteInputs:
				int byteCount = (buffer.get() & 0xFF);
				length++;

				length += byteCount;
				break;

			case WriteSingleCoil:
			case WriteSingleRegister:
			case WriteMultipleCoils:
			case WriteMultipleRegisters:
				length += 4;
				break;

			case MaskWriteRegister:
				length += 6;
				break;

			default:
				return -1;
		}

		// 2 bytes for CRC
		return length + 2;
	}

	@Override
	protected Object getRequestKey(byte[] message) {
		return ModbusRTUTransport.RTU_REQUEST_KEY;
	}

	@Override
	public DriverIODelegate getIODelegate() {
		return ioDelegate;
	}

}
