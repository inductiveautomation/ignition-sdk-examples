package com.inductiveautomation.xopc.drivers.modbus2.protocols;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.inductiveautomation.xopc.drivers.modbus2.protocols.RTULayer.RTUTxDelegate;
import com.inductiveautomation.xopc.drivers.modbus2.util.Source;

public class ModbusRTUTransport implements ModbusTransport {

	public static final Object RTU_REQUEST_KEY = new Object();

	private volatile TxPacket packet;
	private volatile RTULayer rtuLayer;
	private volatile ProtocolStack stack;
	private volatile Source<ApplicationData> applicationData = EMPTY_SOURCE;

	private final byte slaveId;

	public ModbusRTUTransport(byte slaveId) {
		this.slaveId = slaveId;
	}

	@Override
	public void setApplicationDataSource(Source<ApplicationData> applicationData) {
		this.applicationData = applicationData;
	}

	@Override
	public TxPacket next() {
		if (packet == null) {
			buildPacket();
		}

		return packet;
	}

	@Override
	public TxPacket current() {
		if (packet == null) {
			buildPacket();
		}

		return packet;
	}

	@Override
	public Object getKey() {
		return RTU_REQUEST_KEY;
	}

	@Override
	public ProtocolStack getProtocolStack() {
		return stack;
	}

	private void buildPacket() {
		if (packet != null) {
			return;
		}

		rtuLayer = new RTULayer(new RTUTxDelegate() {
			@Override
			public byte getSlaveAddress() {
				return slaveId;
			}
		});

		LinkedList<ProtocolLayer> layers = new LinkedList<ProtocolLayer>();
		layers.add(rtuLayer);
		stack = new ProtocolStack(layers);

		packet = stack.transmit(applicationData.get());
	}

	private static final Source<ApplicationData> EMPTY_SOURCE = new EmptyApplicationDataSource();

	private static class EmptyApplicationDataSource implements Source<ApplicationData> {
		@Override
		public ApplicationData get() {
			Logger.getLogger(getClass()).warn(
					"Using empty ApplicationData. Perhaps someone forget to call setApplicationData()?");
			return ApplicationData.EMPTY;
		}
	}

}
