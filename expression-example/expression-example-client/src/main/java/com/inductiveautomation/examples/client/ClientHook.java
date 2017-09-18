package com.inductiveautomation.examples.client;

import com.inductiveautomation.examples.MultiplyFunction;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client hook configured to add the expression, allowing the expression to be used in a property binding.
 */
public class ClientHook extends AbstractClientModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void startup(ClientContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void configureFunctionFactory(ExpressionFunctionManager factory) {
        factory.getCategories().add("Extended");

        // Adds the exampleMultiply expression under the Extended expression category.
        factory.addFunction("exampleMultiply", "Extended", new MultiplyFunction());
        super.configureFunctionFactory(factory);
    }

}
