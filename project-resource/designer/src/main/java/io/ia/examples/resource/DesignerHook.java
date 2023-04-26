package io.ia.examples.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.swing.Icon;

import com.inductiveautomation.ignition.client.icons.VectorIcons;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

public class DesignerHook extends AbstractDesignerModuleHook {
    public static final VectorIcons ICONS;

    static {
        try (InputStream stream = DesignerHook.class.getResourceAsStream("vector-icons.json")) {
            ICONS = VectorIcons.createFromJson(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize vector icon source", ex);
        }
    }

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
            return ICONS.getIcon("python");
        } else {
            return null;
        }
    }
}
