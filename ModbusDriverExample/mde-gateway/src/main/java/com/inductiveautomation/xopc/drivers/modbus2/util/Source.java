package com.inductiveautomation.xopc.drivers.modbus2.util;

public interface Source<T> {

	/**
	 * @return Some T from the underlying source. Guarantees about this T are implementation
	 *         specific.
	 */
	public T get();

}
