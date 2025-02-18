package com.inductiveautomation.ignition.examples.eventstream.handler.designer;

import com.inductiveautomation.eventstream.designer.EventStreamDesignerHook;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.examples.eventstream.handler.ExampleHandlerModule;

public class ExampleHandlerDesignerHook extends AbstractDesignerModuleHook {

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);

        if (context.getModule(ExampleHandlerModule.MODULE_ID) != null) {
            var hook = EventStreamDesignerHook.get(context);
            if (hook != null) {
                hook.getEventStreamManager().getHandlerRegistry().register(
                    new ExampleHandlerDesignDelegate()
                );
            }
        }
    }
}
