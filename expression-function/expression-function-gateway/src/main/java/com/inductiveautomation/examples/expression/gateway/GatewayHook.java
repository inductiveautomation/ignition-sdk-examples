package com.inductiveautomation.examples.expression.gateway;

import com.inductiveautomation.examples.expression.common.MultiplyFunction;
import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gateway module hook configured to add the expression, allowing the expression to be used in gateway expression tags.
 */
public class GatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setup(GatewayContext gatewayContext) {

    }

    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void configureFunctionFactory(ExpressionFunctionManager factory) {
        factory.getCategories().add("Extended");

        // Adds the exampleMultiply expression under the Extended expression category.
        factory.addFunction("exampleMultiply", "Extended", new MultiplyFunction());
        super.configureFunctionFactory(factory);
    }

}
