package io.ia.examples.resource;

import java.util.List;

import com.inductiveautomation.ignition.common.project.RuntimeProject;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.gateway.project.ProjectLifecycle;

public class EventHandlerLifecycle extends ProjectLifecycle {
    public EventHandlerLifecycle(RuntimeProject project) {
        super(project);
    }

    @Override
    protected void onStartup(List<ProjectResource> list) {

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
