package org.fakester.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.perspective.common.api.ComponentRegistry;
import org.fakester.common.component.display.Image;

public class RadDesignerHook extends AbstractDesignerModuleHook {


    public RadDesignerHook() {
        LoggerEx.newBuilder().build("RadComponents").info("Registering Rad Components in Designer!");
        ComponentRegistry.registerComponent(Image.DESCRIPTOR);
    }


    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {

    }

    @Override
    public void shutdown() {
    }
}
