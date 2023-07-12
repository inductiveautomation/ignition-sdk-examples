package io.ia.examples.resource;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceBuilder;
import com.inductiveautomation.ignition.common.project.resource.ResourcePath;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.tabbedworkspace.NewResourceAction;
import com.inductiveautomation.ignition.designer.tabbedworkspace.ResourceDescriptor;
import com.inductiveautomation.ignition.designer.tabbedworkspace.ResourceEditor;
import com.inductiveautomation.ignition.designer.tabbedworkspace.ResourceFolderNode;
import com.inductiveautomation.ignition.designer.tabbedworkspace.TabbedResourceWorkspace;
import com.inductiveautomation.ignition.designer.workspacewelcome.RecentlyModifiedTablePanel;
import com.inductiveautomation.ignition.designer.workspacewelcome.ResourceBuilderDelegate;
import com.inductiveautomation.ignition.designer.workspacewelcome.ResourceBuilderPanel;
import com.inductiveautomation.ignition.designer.workspacewelcome.WorkspaceWelcomePanel;
import org.jetbrains.annotations.NotNull;

import static com.inductiveautomation.ignition.common.BundleUtil.i18n;

public class PythonResourceWorkspace extends TabbedResourceWorkspace {
    public static final ResourceDescriptor DESCRIPTOR = ResourceDescriptor.builder()
        .resourceType(PythonResource.RESOURCE_TYPE)
        .nounKey("pr.handler.noun")
        .icon(DesignerHook.RESOURCE_ICON)
        .rootFolderText("Custom Event Handlers")
        .rootIcon(DesignerHook.RESOURCE_ICON)
        .build();

    private static final String WORKSPACE_KEY = "python-example-workspace-key";

    private static class NewPythonResourceAction extends NewResourceAction {
        public NewPythonResourceAction(TabbedResourceWorkspace workspace, ResourceFolderNode folder) {
            super(workspace, folder, defaultPythonResource());
        }

        @Override
        protected String newResourceName() {
            return "python";
        }
    }

    public PythonResourceWorkspace(DesignerContext context) {
        super(context, DESCRIPTOR);
    }

    @Override
    protected ResourceEditor<PythonResource> newResourceEditor(ResourcePath resourcePath) {
        return new PythonResourceEditor(this, resourcePath);
    }

    @Override
    public void addNewResourceActions(ResourceFolderNode resourceFolderNode, JPopupMenu jPopupMenu) {
        jPopupMenu.add(new NewPythonResourceAction(this, resourceFolderNode));
    }

    @Override
    public String getKey() {
        return WORKSPACE_KEY;
    }

    @Override
    protected Optional<JComponent> createWorkspaceHomeTab() {
        return Optional.of(new WorkspaceWelcomePanel(
            i18n("pr.resource.category"),
            null,
            null
        ) {
            @Override
            protected List<JComponent> createPanels() {
                return List.of(
                    new ResourceBuilderPanel(
                        context,
                        i18n("pr.handler.noun"),
                        PythonResource.RESOURCE_TYPE.rootPath(),
                        List.of(
                            ResourceBuilderDelegate.build(defaultPythonResource())
                        ),
                        PythonResourceWorkspace.this::open
                    ),
                    new RecentlyModifiedTablePanel(
                        context,
                        PythonResource.RESOURCE_TYPE,
                        i18n("pr.handler.nouns"),
                        PythonResourceWorkspace.this::open
                    )
                );
            }
        });
    }

    @NotNull
    private static Consumer<ProjectResourceBuilder> defaultPythonResource() {
        return PythonResource.toResource(
            new PythonResource("\tpass", true)
        );
    }
}
