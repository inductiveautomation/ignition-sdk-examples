package com.inductiveautomation.ignition.examples.eventstream.source.designer;

import com.inductiveautomation.eventstream.designer.EventStreamDesignerHook;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceModule;

public class ExampleSourceDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);

        // checks if the event stream module is installed
        if (context.getModule(ExampleSourceModule.EVENT_STREAM_MODULE_ID) != null) {
            var hook = EventStreamDesignerHook.get(context);
            if (hook != null) {
                hook.getEventStreamManager().getSourceRegistry().register(
                    new ExampleSourceDesignDelegate()
                );
            }
        }
    }
}
