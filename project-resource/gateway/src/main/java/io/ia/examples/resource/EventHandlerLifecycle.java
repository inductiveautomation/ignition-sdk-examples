package io.ia.examples.resource;

import java.util.List;

import com.inductiveautomation.ignition.common.project.RuntimeProject;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectLifecycle;

public class EventHandlerLifecycle extends ProjectLifecycle {
    private final GatewayContext context;

    public EventHandlerLifecycle(RuntimeProject project, GatewayContext context) {
        super(project);
        this.context = context;
    }

    @Override
    protected void onStartup(List<ProjectResource> list) {
        for (ProjectResource resource : list) {
            if (resource.getResourceType().equals(PythonResource.RESOURCE_TYPE)) {
                var pythonResource = PythonResource.fromResource(resource);
                // do something with the resource
            }
        }
    }

    @Override
    protected void onShutdown(List<ProjectResourceId> list) {

    }

    @Override
    protected void onResourcesCreated(List<ProjectResource> list) {

    }

    @Override
    protected void onResourcesModified(List<ProjectResource> list) {

    }

    @Override
    protected void onResourcesDeleted(List<ProjectResourceId> list) {

    }
}
