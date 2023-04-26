package io.ia.examples.resource;

import javax.swing.Icon;

import com.inductiveautomation.ignition.client.icons.SvgIconUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

public class DesignerHook extends AbstractDesignerModuleHook {
    public static final Icon RESOURCE_ICON = SvgIconUtil.getIcon(DesignerHook.class, "code-block");
    private DesignerContext context;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        this.context = context;

        context.registerResourceWorkspace(new PythonResourceWorkspace(context));
    }

    @Override
    public void shutdown() {
    }

    @Override
    public String getResourceCategoryKey(ProjectResourceId id) {
        return super.getResourceCategoryKey(id);
    }

    @Override
    public String getResourceDisplayName(ProjectResourceId id) {
        return super.getResourceDisplayName(id);
    }

    @Override
    public Icon getResourceIcon(ProjectResourceId id) {
        if (id.getResourceType().equals(PythonResource.RESOURCE_TYPE)) {
            return RESOURCE_ICON;
        } else {
            return null;
        }
    }
}
