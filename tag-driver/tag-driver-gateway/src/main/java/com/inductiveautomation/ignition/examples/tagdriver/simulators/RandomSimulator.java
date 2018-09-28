package com.inductiveautomation.ignition.examples.tagdriver.simulators;

import java.util.Random;

import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public class RandomSimulator extends AbstractSimulator {
    private Random random;
    private SimulatorTypes simType;

    public RandomSimulator(String name, SimulatorTypes type) {
        this(name, type.dataType);
        random = new Random();
        simType = type;
    }

    public RandomSimulator(String name, BuiltinDataType dataType) {
        super(name, dataType);
    }

    @Override
    public void tick() {
        this.currentValue = new Variant(random.nextDouble());
    }

    private enum SimulatorTypes {
        BOOLEAN(BuiltinDataType.Boolean),
        INT(BuiltinDataType.Int16),
        LONG(BuiltinDataType.Int32),
        FLOAT(BuiltinDataType.Float),
        DOUBLE(BuiltinDataType.Double);

        private final BuiltinDataType dataType;

        SimulatorTypes(BuiltinDataType dataType) {
            this.dataType = dataType;
        }
    }
}
