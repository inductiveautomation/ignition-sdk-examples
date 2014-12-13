package com.inductiveautomation.xopc.drivers.modbus2.serial;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import Serialio.SerialConfig;
import Serialio.SerialPortLocal;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.inductiveautomation.ignition.common.util.ByteArrayPool;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.iosession.async.IOEventHandler;
import com.inductiveautomation.iosession.serialio.AsyncSerialIOSession;
import com.inductiveautomation.xopc.driver.api.DriverIODelegate;
import com.inductiveautomation.xopc.driver.util.ByteUtilities;

public class SerialIODelegate implements DriverIODelegate {

	private final Object IO_SESSION_LOCK = new Object();
	private AsyncSerialIOSession ioSession;

	private final LoggerEx log;

	private final ModbusRTUDriver driver;
	private final SerialConfig serialConfig;

	public SerialIODelegate(ModbusRTUDriver driver, SerialConfig serialConfig) {
		this.driver = driver;
		this.serialConfig = serialConfig;

		String logName = String.format("%s.%s", driver.getLogger().getName(), getClass().getSimpleName());
		log = new LoggerEx(Logger.getLogger(logName));
		log.setIdentObject(new Object() {
			@SuppressWarnings("unused")
			final String serialPort = SerialIODelegate.this.serialConfig.getPortNameString();
		});
	}

	@Override
	public void connect() throws IOException {
		synchronized (IO_SESSION_LOCK) {
			SerialPortLocal serialPort = new SerialPortLocal(serialConfig);
			ioSession = new AsyncSerialIOSession(serialPort, new ThreadFactoryBuilder().build());

			ioSession.setEventHandler(new DriverIOEventHandler());
			ioSession.start();
		}
	}

	@Override
	public void disconnect() {
		synchronized (IO_SESSION_LOCK) {
			if (ioSession != null) {
				ioSession.stop();
			}
		}
	}

	@Override
	public void writeToIO(ByteBuffer... buffers) {
		if (buffers == null || buffers.length < 1) {
			return;
		}

		synchronized (IO_SESSION_LOCK) {
			for (ByteBuffer buf : buffers) {
				if (log.isTraceEnabled()) {
					log.trace("Sending ByteBuffer " + ByteUtilities.toString(buf));
				}

				try {
					ioSession.write(buf);
				} catch (IOException e) {
					log.error("IOException while writing.", e);
				}
			}
		}
	}

	@Override
	public void onNotifyConnectFailed(Exception connectError) {
		synchronized (IO_SESSION_LOCK) {
			if (ioSession != null) {
				ioSession.stop();
			}
		}
	}

	@Override
	public void onNotifyConnectSucceeded() {
	}

	@Override
	public void onNotifyConnectionLost() {
	}

	@Override
	public void onNotifyDisconnectDone() {
	}

	private class DriverIOEventHandler implements IOEventHandler {

		private static final int BUFFER_SIZE = 1024 * 8;

		private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		private final ByteArrayPool arrayPool = new ByteArrayPool();

		@Override
		public final void dataArrived(byte[] data, int bytesRead) {
			buffer.put(data, 0, bytesRead);

			buffer.flip();

			int length = getLengthAndResetPosition(buffer);

			while (length > 0 && buffer.remaining() >= length) {
				byte[] msg = arrayPool.takeArray(length);
				buffer.get(msg);
				deliverMessage(msg);

				length = buffer.hasRemaining() ? getLengthAndResetPosition(buffer) : -1;
			}

			buffer.compact();
		}

		private final int getLengthAndResetPosition(ByteBuffer buffer) {
			int p = buffer.position();
			int length = driver._messageLength(buffer);
			buffer.position(p);
			return length;
		}

		private final void deliverMessage(final byte[] msg) {
			driver.getExecutor().execute(new Runnable() {
				@Override
				public void run() {
					driver._messageArrived(msg);
					arrayPool.returnArray(msg);
				}
			});
		}

		@Override
		public void connectionLost(IOException e) {
			log.error(String.format("Serial connection closed, DriverState was %s.", driver.getDriverState()), e);

			driver.notifyConnectionLost();
		}
	}

}
