package io.ia.examples.resource;

import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceBuilder;
import com.inductiveautomation.ignition.common.project.resource.ResourcePath;
import com.inductiveautomation.ignition.designer.gui.tools.ExtensionFunctionPanel;
import com.inductiveautomation.ignition.designer.tabbedworkspace.ResourceEditor;
import net.miginfocom.swing.MigLayout;

public class PythonResourceEditor extends ResourceEditor<PythonResource> {

    private final ExtensionFunctionPanel extensionFunctionPanel;

    public PythonResourceEditor(PythonResourceWorkspace workspace, ResourcePath resourcePath) {
        super(workspace, resourcePath);

        setLayout(new MigLayout("ins 16, fill"));
        extensionFunctionPanel = new ExtensionFunctionPanel(
            ExtensionFunctionPanel.GATEWAY_HINTS,
            PythonResource.FUNCTION_DESCRIPTOR
        );
        extensionFunctionPanel.setEnabled(false);

        add(extensionFunctionPanel, "push, grow");
    }

    @Override
    protected void init(PythonResource pythonResource) {
        extensionFunctionPanel.setUserScript(pythonResource.getUserCode());
        extensionFunctionPanel.setEnabled(true);
    }

    @Override
    protected PythonResource deserialize(ProjectResource resource) {
        return PythonResource.fromResource(resource);
    }

    @Override
    protected PythonResource getObjectForSave() {
        return new PythonResource(resourcePath.getName(), extensionFunctionPanel.getUserScript());
    }

    @Override
    protected void serializeResource(ProjectResourceBuilder builder, PythonResource object) {
        PythonResource.toResource(object).accept(builder);
    }
}
