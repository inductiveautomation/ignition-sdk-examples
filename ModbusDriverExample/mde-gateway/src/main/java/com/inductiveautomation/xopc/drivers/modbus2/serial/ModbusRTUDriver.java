package com.inductiveautomation.xopc.drivers.modbus2.serial;

import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledExecutorService;

import Serialio.SerialConfig;
import com.inductiveautomation.xopc.driver.api.DriverContext;
import com.inductiveautomation.xopc.driver.api.DriverIODelegate;
import com.inductiveautomation.xopc.drivers.modbus2.AbstractModbusDriver;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.ModbusRtuDriverSettings;
import com.inductiveautomation.xopc.drivers.modbus2.factories.ModbusTransportFactory;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusRTUTransport;
import com.inductiveautomation.xopc.drivers.modbus2.protocols.ModbusTransport;
import com.inductiveautomation.xopc.drivers.modbus2.structs.FunctionCode;

public class ModbusRTUDriver extends AbstractModbusDriver {

	private volatile SerialIODelegate ioDelegate;

	public ModbusRTUDriver(DriverContext driverContext, ModbusRtuDriverSettings settings) {
		super(driverContext, settings);

		SerialConfig serialConfig = new SerialConfig(settings.getSerialPort());

		serialConfig.setBitRate(settings.getBitRate().getConfigValue());
		serialConfig.setDataBits(settings.getDataBits().getConfigValue());
		serialConfig.setParity(settings.getParity().getConfigValue());
		serialConfig.setStopBits(settings.getStopBits().getConfigValue());
		serialConfig.setHandshake(settings.getHandshake().getConfigValue());

		ioDelegate = new SerialIODelegate(this, serialConfig);
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
	public DriverIODelegate getIODelegate() {
		return ioDelegate;
	}

	@Override
	protected Object getRequestKey(byte[] message) {
		return ModbusRTUTransport.RTU_REQUEST_KEY;
	}

	// --- These exist to thwart stupid visibility problems in SerialIODelegate.

	ScheduledExecutorService getExecutor() {
		return executor;
	}

	int _messageLength(ByteBuffer buffer) {
		return messageLength(buffer);
	}

	void _messageArrived(byte[] message) {
		super.messageArrived(message);
	}

}
