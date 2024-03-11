package io.ia.examples.resource;

import javax.swing.JCheckBox;

import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceBuilder;
import com.inductiveautomation.ignition.common.project.resource.ResourcePath;
import com.inductiveautomation.ignition.designer.gui.tools.ExtensionFunctionPanel;
import com.inductiveautomation.ignition.designer.tabbedworkspace.ResourceEditor;
import net.miginfocom.swing.MigLayout;

import static com.inductiveautomation.ignition.common.BundleUtil.i18n;

public class PythonResourceEditor extends ResourceEditor<PythonResource> {

    private ExtensionFunctionPanel extensionFunctionPanel;
    private JCheckBox enabledCheckBox;

    public PythonResourceEditor(PythonResourceWorkspace workspace, ResourcePath resourcePath) {
        super(workspace, resourcePath);
    }

    @Override
    protected void init(PythonResource resource) {
        removeAll();
        setLayout(new MigLayout("ins 16, fill"));

        enabledCheckBox = new JCheckBox(i18n("words.enabled"));
        add(enabledCheckBox, "wrap");
        extensionFunctionPanel = new ExtensionFunctionPanel(ExtensionFunctionPanel.GATEWAY_HINTS);
        extensionFunctionPanel.setDescriptor(PythonResource.FUNCTION_DESCRIPTOR);
        extensionFunctionPanel.setUserScript(resource.getUserCode());

        add(extensionFunctionPanel, "push, grow");
    }

    /*
     Preferred pattern is to override deserialize(ProjectResource) so that you can store configuration data using
     whatever resource keys you prefer.
     */
    @Override
    protected PythonResource deserialize(ProjectResource resource) {
        return PythonResource.fromResource(resource);
    }

    @Override
    protected PythonResource getObjectForSave() {
        return new PythonResource(extensionFunctionPanel.getUserScript(), enabledCheckBox.isSelected());
    }

    // Don't save changes if getObjectForSave() is equal to the cached T
    @Override
    protected boolean isOptimizeCommits() {
        return true;
    }

    /*
    Preferred pattern is to override serializeResource(builder, T) so that you can serialize in whatever custom
    format you want, rather than relying on Java serialization of your resource class into a data.bin file.

    This allows human-readable diffing and easier source control compatibility.
    */
    @Override
    protected void serializeResource(ProjectResourceBuilder builder, PythonResource object) {
        PythonResource.toResource(object).accept(builder);
    }
}
