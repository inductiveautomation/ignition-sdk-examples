package com.inductiveautomation.examples.scriptingrpc;

import java.util.Random;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.expressions.Expression;
import com.inductiveautomation.ignition.common.expressions.ExpressionException;
import com.inductiveautomation.ignition.common.expressions.functions.AbstractFunction;
import com.inductiveautomation.ignition.common.model.values.BasicQualifiedValue;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;

public class RandomFunction extends AbstractFunction {

    private static final Random RANDOM = new Random();

    @Override
    public QualifiedValue execute(Expression[] args) throws ExpressionException {
        QualifiedValue qv = args[0].execute();
        int bound = TypeUtilities.toInteger(qv.getValue());

        synchronized (RANDOM) {
            return new BasicQualifiedValue(RANDOM.nextInt(bound), qv.getQuality());
        }
    }

    @Override
    public String getArgDocString() {
        return "random";
    }

    @Override
    protected String getFunctionDisplayName() {
        return "random";
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }

    @Override
    protected boolean validateNumArgs(int num) {
        return num == 1;
    }

}
