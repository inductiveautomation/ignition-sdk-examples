package io.ia.examples.resource;

import javax.swing.*;
import com.inductiveautomation.ignition.common.resourcecollection.Resource;
import com.inductiveautomation.ignition.common.resourcecollection.ResourceBuilder;
import com.inductiveautomation.ignition.common.resourcecollection.ResourcePath;
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
        enabledCheckBox.setSelected(resource.enabled());
        add(enabledCheckBox, "wrap");
        extensionFunctionPanel = new ExtensionFunctionPanel(ExtensionFunctionPanel.GATEWAY_HINTS);
        extensionFunctionPanel.setDescriptor(PythonResource.FUNCTION_DESCRIPTOR);
        extensionFunctionPanel.setUserScript(resource.userCode());

        add(extensionFunctionPanel, "push, grow");
    }

    /*
     Preferred pattern is to override deserialize(Resource) so that you can store configuration data using
     whatever resource keys you prefer.
     */
    @Override
    protected PythonResource deserialize(Resource resource) {
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
    format you want.

    This allows human-readable diffing and easier source control compatibility.
    */
    @Override
    protected void serializeResource(ResourceBuilder builder, PythonResource object) {
        PythonResource.toResource(object).accept(builder);
    }
}
