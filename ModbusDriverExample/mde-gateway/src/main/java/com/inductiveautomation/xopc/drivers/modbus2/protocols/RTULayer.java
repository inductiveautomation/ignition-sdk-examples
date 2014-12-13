package com.inductiveautomation.xopc.drivers.modbus2.protocols;

import java.nio.ByteBuffer;

import com.inductiveautomation.xopc.drivers.modbus2.util.CRC16;

public class RTULayer implements ProtocolLayer {

	private final CRC16 crc = new CRC16();

	private final RTUTxDelegate txDelegate;

	public RTULayer(RTUTxDelegate txDelegate) {
		this.txDelegate = txDelegate;
	}

	@Override
	public int headerLength() {
		return 1;
	}

	@Override
	public int footerLength() {
		return 2;
	}

	@Override
	public int length() {
		return headerLength() + footerLength();
	}

	@Override
	public void transmit(TxPacket packet) {
		addHeader(packet);
		addFooter(packet);
	}

	private void addFooter(TxPacket packet) {
		int currentLength = packet.currentLength();
		byte[] bs = new byte[currentLength];
		packet.getBuffer().get(bs, 0, currentLength);

		crc.reset();
		crc.update(bs, 0, currentLength);

		ByteBuffer footer = packet.addFooter(this);

		footer.put((byte) ((crc.getValue() >> 0) & 0xFFL));
		footer.put((byte) ((crc.getValue() >> 8) & 0xFFL));
	}

	private void addHeader(TxPacket packet) {
		ByteBuffer header = packet.addHeader(this);
		header.put(txDelegate.getSlaveAddress());
	}

	@Override
	public void receive(RxPacket packet) {
		packet.markHeader(this);
		packet.markFooter(this);
	}

	public static interface RTUTxDelegate {

		public byte getSlaveAddress();

	}

}
