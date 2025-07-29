package com.inductiveautomation.ignition.examples.tagdriver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

class ValueSimulator implements Runnable {

  private final Map<String, Variant> trackedValues = new ConcurrentHashMap<>();

  public ValueSimulator() {}

  public void addTrackedValue(String key, Long initial) {
    trackedValues.put(key, Variant.ofInt64(initial));
  }

  public DataValue getTrackedValue(String key) {
    Variant current = trackedValues.get(key);
    if (current != null) {
      return new DataValue(current);
    } else {
      return new DataValue(Variant.NULL_VALUE, StatusCode.BAD);
    }
  }

  @Override
  public void run() {
    for (Map.Entry<String, Variant> entry : trackedValues.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      Long nextValue = 0L;
      if (value instanceof Variant variant && variant.getValue() instanceof Long currentValue) {
        nextValue = currentValue + 1L;
      }

      trackedValues.put(key, Variant.ofInt64(nextValue));
    }
  }

}
