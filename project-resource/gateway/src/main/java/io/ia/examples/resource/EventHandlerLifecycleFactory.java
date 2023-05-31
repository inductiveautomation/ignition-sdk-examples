package io.ia.examples.resource;

import java.util.List;

import com.inductiveautomation.ignition.common.project.RuntimeProject;
import com.inductiveautomation.ignition.common.script.ModuleLibrary;
import com.inductiveautomation.ignition.common.script.ScriptLibrary;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectLifecycleFactory;
import com.inductiveautomation.ignition.gateway.project.ProjectManagerBase;
import com.inductiveautomation.ignition.gateway.project.ResourceFilter;

public class EventHandlerLifecycleFactory extends ProjectLifecycleFactory<EventHandlerLifecycle> {
    private final GatewayContext context;

    public EventHandlerLifecycleFactory(GatewayContext context) {
        super(context.getProjectManager());
        this.context = context;
    }

    @Override
    public EventHandlerLifecycle createProjectLifecycle(RuntimeProject runtimeProject) {
        return new EventHandlerLifecycle(runtimeProject, context);
    }

    @Override
    protected ResourceFilter getResourceFilter() {
        return ResourceFilter.newBuilder()
            .addResourceTypes(List.of(
                PythonResource.RESOURCE_TYPE,
                ModuleLibrary.RESOURCE_TYPE,
                ScriptLibrary.RESOURCE_TYPE
            ))
            .build();
    }
}
