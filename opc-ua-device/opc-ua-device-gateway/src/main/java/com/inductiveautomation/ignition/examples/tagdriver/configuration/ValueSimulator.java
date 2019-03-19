package com.inductiveautomation.ignition.examples.tagdriver.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public class ValueSimulator implements Runnable {

    private final Map<String, Variant> trackedValues = new ConcurrentHashMap<>();

    public ValueSimulator() {}

    public void addTrackedValue(String key, long initial) {
        trackedValues.put(key, new Variant(initial));
    }

    public DataValue getTrackedValue(String key) {
        Variant current = trackedValues.get(key);
        if (current != null) {
            return new DataValue(current);
        } else {
            return new DataValue(new Variant(null), StatusCode.BAD);
        }
    }

    @Override
    public void run() {
        for (Map.Entry<String, Variant> stringVariantEntry : trackedValues.entrySet()) {
            String key = stringVariantEntry.getKey();
            Variant currentValue = stringVariantEntry.getValue();
            trackedValues.put(key, new Variant((long) currentValue.getValue() + 1));
        }
    }

}
