package io.ia.examples.resource;

import javax.swing.Icon;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.resourcecollection.ResourcePath;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.navtree.icon.InteractiveSvgIcon;

import static com.inductiveautomation.ignition.common.BundleUtil.i18n;

public class DesignerHook extends AbstractDesignerModuleHook {
    public static final Icon RESOURCE_ICON;

    static {
        RESOURCE_ICON = InteractiveSvgIcon.createIconAll(
            DesignerHook.class, "code-block.svg", 16, 16, true
        );
    }

    private DesignerContext context;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        this.context = context;

        PythonResourceWorkspace workspace = new PythonResourceWorkspace(context);
        context.registerResourceWorkspace(workspace);

        BundleUtil.get().addBundle("pr", DesignerHook.class, "designer");

        context.registerSearchProvider(new HandlerSearchProvider(context, workspace));
    }

    @Override
    public void shutdown() {
    }

    @Override
    public String getResourceCategoryKey(ResourcePath id) {
        if (id.getResourceType().equals(PythonResource.RESOURCE_TYPE)) {
            return i18n("pr.resource.category");
        } else {
            return null;
        }
    }

    @Override
    public Icon getResourceIcon(ResourcePath id) {
        if (id.getResourceType().equals(PythonResource.RESOURCE_TYPE)) {
            return RESOURCE_ICON;
        } else {
            return null;
        }
    }
}
