package io.ia.examples.resource;

import com.inductiveautomation.ignition.common.gui.progress.TaskProgressListener;
import com.inductiveautomation.ignition.designer.findreplace.AbstractSearchProvider;
import com.inductiveautomation.ignition.designer.findreplace.DefaultSearchObject;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import org.python.util.PythonObjectInputStream;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static io.ia.examples.resource.Constants.MODULE_ID;

class HandlerSearchProvider extends AbstractSearchProvider {
    private final DesignerContext context;
    private final PythonResourceWorkspace workspace;

    public HandlerSearchProvider(DesignerContext context,
                                 PythonResourceWorkspace workspace) {
        this.context = context;
        this.workspace = workspace;
    }

    @Override
    public String getName() {
        return "Custom Event Handlers";
    }

    @Override
    public String getId() {
        return MODULE_ID;
    }

    @Override
    public List<Object> getCategories() {
        return null;
    }

    @Override
    public boolean hasSelectableObjects() {
        return false;
    }

    @Override
    public void selectObjects(SelectedObjectsHandler selectedObjectsHandler) {

    }

    @Override
    protected String getSelectableObjectBaseKey() {
        return null;
    }

    @Override
    public Iterator<SearchObject> retrieveSearchableObjects(Collection<Object> categories,
                                                            List<Object> selectedObjects,
                                                            TaskProgressListener taskProgressListener) {
        taskProgressListener.setIndeterminate(true);
        // we're ignoring the category and selection passthroughs to implement the most basic form of find & replace
        return context.getProject()
                .getResourcesOfType(PythonResource.RESOURCE_TYPE)
                .stream()
                .map(resource -> {
                    var pythonResource = PythonResource.fromResource(resource);
                    return DefaultSearchObject.newBuilder()
                            .setPropertyName("script")
                            .setLocation(resource.getFolderPath())
                            .setMutable(true)
                            .setValue(pythonResource.getUserCode())
                            .onLocate(() -> workspace.open(resource.getResourcePath()))
                            .whenAltered(newValue -> {
                                PythonResource updatedResource =
                                        new PythonResource(newValue, pythonResource.isEnabled());
                                context.getProject().createOrModify(resource.getResourcePath(),
                                        PythonResource.toResource(updatedResource));
                            })
                            .build();
                })
                .iterator();
    }
}
