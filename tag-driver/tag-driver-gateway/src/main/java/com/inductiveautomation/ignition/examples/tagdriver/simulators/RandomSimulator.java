package com.inductiveautomation.ignition.examples.tagdriver.simulators;

import java.util.Random;

import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.jetbrains.annotations.NotNull;

public class RandomSimulator extends AbstractSimulator {
    private Random random;
    private SimulatorTypes simType;

    public RandomSimulator(String name, SimulatorTypes type) {
        this(name, getBuiltinDataType(type));
        random = new Random();
        simType = type;
    }

    @NotNull
    private static BuiltinDataType getBuiltinDataType(SimulatorTypes type) throws IllegalArgumentException {
        BuiltinDataType dataType;
        switch (type) {
            case BOOLEAN:
                dataType = BuiltinDataType.Boolean;
                break;
            case INT:
                dataType = BuiltinDataType.UInt16;
                break;
            case LONG:
                dataType = BuiltinDataType.UInt32;
                break;
            case FLOAT:
                dataType = BuiltinDataType.Float;
                break;
            case DOUBLE:
                dataType = BuiltinDataType.Double;
                break;
            default:
                throw new IllegalArgumentException("Invalid simulator type");
        }
        return dataType;
    }

    public RandomSimulator(String name, BuiltinDataType dataType) {
        super(name, dataType);
    }

    @Override
    public void tick() {
        this.currentValue = new Variant(random.nextDouble());
    }

    private enum SimulatorTypes {
        BOOLEAN,
        INT,
        LONG,
        FLOAT,
        DOUBLE
    }
}
