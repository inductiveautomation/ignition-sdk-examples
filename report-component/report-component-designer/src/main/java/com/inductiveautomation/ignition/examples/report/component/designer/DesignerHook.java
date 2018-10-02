package com.inductiveautomation.ignition.examples.report.component.designer;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.reporting.designer.api.DesignerShapeRegistry;
import com.inductiveautomation.ignition.examples.report.component.shapes.Smiley;
import com.inductiveautomation.rm.archiver.RMArchiver;

/**
 * @author Perry Arellano-Jones
 */

    public class DesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
        public static final String BUNDLE_NAME = "component";

        @Override
        public void startup(DesignerContext context, LicenseState activationState) throws Exception {
            super.startup(context, activationState);

            /* add our bundle to centralize strings and allow i18n support */
            BundleUtil.get().addBundle(BUNDLE_NAME, DesignerHook.class.getClassLoader(), BUNDLE_NAME);

            /* This is where our new shape registered for the Report Designer */
            RMArchiver.registerClass(Smiley.ARCHIVE_NAME, Smiley.class);
            DesignerShapeRegistry.get(context).register(Smiley.class);
        }
    }

