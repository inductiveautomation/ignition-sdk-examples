package com.inductiveautomation.xopc.drivers.modbus2.protocols;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.inductiveautomation.xopc.drivers.modbus2.structs.MBAPHeader;
import com.inductiveautomation.xopc.drivers.modbus2.util.Sequence;
import com.inductiveautomation.xopc.drivers.modbus2.util.Source;

public class ModbusTCPTransport implements ModbusTransport {

	private volatile TxPacket packet;
	private volatile MBAPLayer mbapLayer;
	private volatile ProtocolStack stack;
	private volatile short key;
	private volatile Source<ApplicationData> applicationData = EMPTY_SOURCE;

	private final Sequence<Short> keys;
	private final byte unitId;

	public ModbusTCPTransport(Sequence<Short> keys, byte unitId) {
		this.keys = keys;
		this.unitId = unitId;
	}

	@Override
	public void setApplicationDataSource(Source<ApplicationData> applicationData) {
		this.applicationData = applicationData;
	}

	@Override
	public TxPacket next() {
		if (packet == null) {
			buildPacket();
		} else {
			updateKey();
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
		return key;
	}

	@Override
	public ProtocolStack getProtocolStack() {
		return stack;
	}

	/**
	 * This should only be called once.
	 */
	private void buildPacket() {
		if (packet != null) {
			return;
		}

		key = keys.next();

		mbapLayer = new MBAPLayer(new MBAPLayer.MBAPTxDelegate() {
			@Override
			public MBAPHeader getHeader(TxPacket packet) {
				short length = (short) (packet.currentLength() + 1);

				return new MBAPHeader(
						key,
						MBAPHeader.MODBUS_PROTOCOL_ID,
						length,
						unitId);
			}
		});

		LinkedList<ProtocolLayer> layers = new LinkedList<ProtocolLayer>();
		layers.add(mbapLayer);
		stack = new ProtocolStack(layers);

		packet = stack.transmit(applicationData.get());
	}

	private static final int TRANSACTION_ID_OFFSET = 0;

	/**
	 * Since we know where in the buffer the sequence number is we can replace it directly instead
	 * of building and transmitting a new TxPacket every time.
	 */
	private void updateKey() {
		if (packet != null) {
			key = keys.next();

			ByteBuffer buffer = packet.headerBufferForLayer(mbapLayer);
			int position = buffer.position();
			buffer.position(position + TRANSACTION_ID_OFFSET);
			buffer.putShort(key);
		}
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
