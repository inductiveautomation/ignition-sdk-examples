package com.inductiveautomation.reporting.examples.datasource.designer;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.reporting.designer.api.DesignerDataSourceRegistry;
import com.inductiveautomation.reporting.examples.datasource.designer.ui.RestJsonDataConfigPanel;

/**
 * We use the DesignerHook to add our resource bundles and register our Data Source with the Ignition Designer.
 * This class is the one specified in the Ignition Maven Plugin as the Designer scoped hook in the build
 * pom.xml file.
 * @author Perry Arellano-Jones
 */

public class DesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);

        /* add our bundle to centralize strings and allow i18n support */
        BundleUtil.get().addBundle("datasource", DesignerHook.class.getClassLoader(), "datasource");


        /* This is where our custom data configuration panel is registered for the Report Data panel */
        DesignerDataSourceRegistry.get(context).register(RestJsonDataConfigPanel.FACTORY);
    }
}
