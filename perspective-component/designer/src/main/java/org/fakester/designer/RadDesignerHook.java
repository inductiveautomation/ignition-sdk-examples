package org.fakester.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.perspective.designer.api.PerspectiveDesignerInterface;
import org.fakester.common.component.display.Image;

public class RadDesignerHook extends AbstractDesignerModuleHook {


    public RadDesignerHook() {
        LoggerEx.newBuilder().build("RadComponents").info("Registering Rad Components in Designer!");
    }


    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        PerspectiveDesignerInterface.getComponentRegistry(context)
            .registerComponent(Image.DESCRIPTOR);
    }

    @Override
    public void shutdown() {
    }
}
