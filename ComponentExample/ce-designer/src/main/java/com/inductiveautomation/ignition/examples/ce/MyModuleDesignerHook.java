/* Filename: MyModuleDesignerHook.java
 * Created by Perry Arellano-Jones on 12/11/14.
 * Copyright Inductive Automation 2014
 */
package com.inductiveautomation.ignition.examples.ce;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.examples.ce.components.HelloWorldComponent;
import com.inductiveautomation.vision.api.designer.VisionDesignerInterface;
import com.inductiveautomation.vision.api.designer.palette.JavaBeanPaletteItem;
import com.inductiveautomation.vision.api.designer.palette.Palette;
import com.inductiveautomation.vision.api.designer.palette.PaletteItemGroup;


/**
 * This is the Designer-scope module hook for the component example module for the Ignition SDK.
 */
public class MyModuleDesignerHook extends AbstractDesignerModuleHook {

    public static final String MODULE_ID = "component-example";

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        // Add the BeanInfo package to the search path
        context.addBeanInfoSearchPath("com.inductiveautomation.ignition.examples.ce.beaninfos");

        // Add my component to its own palette
        VisionDesignerInterface sdk = (VisionDesignerInterface) context
                .getModule(VisionDesignerInterface.VISION_MODULE_ID);
        if (sdk != null) {
            Palette palette = sdk.getPalette();

            PaletteItemGroup group = palette.addGroup("Example");
            group.addPaletteItem(new JavaBeanPaletteItem(HelloWorldComponent.class));
        }
    }

}

