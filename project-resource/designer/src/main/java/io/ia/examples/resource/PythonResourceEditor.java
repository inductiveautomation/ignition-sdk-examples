package io.ia.examples.resource;

import javax.swing.JCheckBox;

import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceBuilder;
import com.inductiveautomation.ignition.common.project.resource.ResourcePath;
import com.inductiveautomation.ignition.designer.gui.tools.ExtensionFunctionPanel;
import com.inductiveautomation.ignition.designer.tabbedworkspace.ResourceEditor;
import net.miginfocom.swing.MigLayout;

public class PythonResourceEditor extends ResourceEditor<PythonResource> {

    private ExtensionFunctionPanel extensionFunctionPanel;
    private JCheckBox enabledCheckBox;

    public PythonResourceEditor(PythonResourceWorkspace workspace, ResourcePath resourcePath) {
        super(workspace, resourcePath);
    }

    @Override
    protected void init(PythonResource pythonResource) {
        removeAll();
        setLayout(new MigLayout("ins 16, fill"));

        enabledCheckBox = new JCheckBox("Enabled");
        add(enabledCheckBox, "wrap");
        extensionFunctionPanel = new ExtensionFunctionPanel(ExtensionFunctionPanel.GATEWAY_HINTS);
        extensionFunctionPanel.setDescriptor(PythonResource.FUNCTION_DESCRIPTOR);
        extensionFunctionPanel.setUserScript(pythonResource.getUserCode());

        add(extensionFunctionPanel, "push, grow");
    }

    @Override
    protected PythonResource deserialize(ProjectResource resource) {
        return PythonResource.fromResource(resource);
    }

    @Override
    protected PythonResource getObjectForSave() {
        return new PythonResource(extensionFunctionPanel.getUserScript(), enabledCheckBox.isSelected());
    }

    @Override
    protected boolean isOptimizeCommits() {
        return true;
    }

    @Override
    protected void serializeResource(ProjectResourceBuilder builder, PythonResource object) {
        PythonResource.toResource(object).accept(builder);
    }
}
