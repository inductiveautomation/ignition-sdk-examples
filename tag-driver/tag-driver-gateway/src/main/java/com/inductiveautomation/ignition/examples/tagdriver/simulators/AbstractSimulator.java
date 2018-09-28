package com.inductiveautomation.ignition.examples.tagdriver.simulators;

import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public abstract class AbstractSimulator implements Simulator {
    private final String name;
    private final BuiltinDataType dataType;
    Variant currentValue;

    public AbstractSimulator(String name, BuiltinDataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    @Override
    public abstract void tick();

    @Override
    public Variant getValue() {
        return currentValue;
    }
}
