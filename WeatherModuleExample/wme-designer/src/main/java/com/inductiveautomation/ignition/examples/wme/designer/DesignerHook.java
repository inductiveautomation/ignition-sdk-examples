/* Filename: DesignerHook2.java
 * Created on Jan 22, 2011
 * Author: Kevin Herron
 * Copyright Inductive Automation 2011
 * Project: WeatherModule_Designer
 */
package com.inductiveautomation.ignition.examples.wme.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.examples.wme.client.WeatherComponent;
import com.inductiveautomation.vision.api.designer.VisionDesignerInterface;
import com.inductiveautomation.vision.api.designer.palette.JavaBeanPaletteItem;
import com.inductiveautomation.vision.api.designer.palette.Palette;
import com.inductiveautomation.vision.api.designer.palette.PaletteItemGroup;

public class DesignerHook extends AbstractDesignerModuleHook {

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        context.addBeanInfoSearchPath("com.inductiveautomation.ignition.examples.wme.designer.beaninfo");

        VisionDesignerInterface vdi = (VisionDesignerInterface) context
                .getModule(VisionDesignerInterface.VISION_MODULE_ID);

        if (vdi != null) {
            Palette palette = vdi.getPalette();

            PaletteItemGroup group = palette.addGroup("Weather");
            group.addPaletteItem(new JavaBeanPaletteItem(WeatherComponent.class));
        }

        super.startup(context, activationState);
    }
}
