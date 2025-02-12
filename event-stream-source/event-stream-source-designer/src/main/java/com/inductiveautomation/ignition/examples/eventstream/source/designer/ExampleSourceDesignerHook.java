package com.inductiveautomation.ignition.examples.eventstream.source.designer;

import com.inductiveautomation.eventstream.designer.EventStreamDesignerHook;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

public class ExampleSourceDesignerHook extends AbstractDesignerModuleHook {

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);

        if (context.getModule(com.inductiveautomation.examples.eventstream.source.ExampleSourceModule.MODULE_ID) != null) {
            var hook = EventStreamDesignerHook.get(context);
            if (hook != null) {
                hook.getEventStreamManager().getSourceRegistry().register(new com.inductiveautomation.examples.eventstream.source.designer.ExampleSourceDesignDelegate());
            }
        }
    }
}
