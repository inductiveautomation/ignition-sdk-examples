package com.inductiveautomation.ignition.examples.expression.designer;

import com.inductiveautomation.ignition.examples.expression.common.MultiplyFunction;
import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Designer hook configured to add the expression, allowing the expression to be used in a property binding.
 */
public class DesignerHook extends AbstractDesignerModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
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
