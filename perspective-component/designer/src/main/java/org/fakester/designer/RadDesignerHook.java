package org.fakester.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.perspective.common.api.ComponentRegistry;
import org.fakester.common.component.display.Image;

public class RadDesignerHook extends AbstractDesignerModuleHook {

    public RadDesignerHook(){

    }

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {

        ComponentRegistry.registerComponent(Image.DESCRIPTOR);
    }

    @Override
    public void shutdown() {
    }
}
