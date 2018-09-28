package com.inductiveautomation.ignition.examples.tagdriver.simulators;

import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public interface Simulator {
    public void tick();

    public Variant getValue();
}
