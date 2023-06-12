package io.ia.examples.resource;

import java.util.List;
import java.util.function.Predicate;

import com.inductiveautomation.ignition.common.project.RuntimeProject;
import com.inductiveautomation.ignition.common.script.ModuleLibrary;
import com.inductiveautomation.ignition.common.script.ScriptLibrary;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectLifecycleFactory;
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

    // Every time any of these resources change (in any project that meets the project filter), the lifecycle will be
    // notified.
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

    // Entrypoint to customize projects to 'listen' to. Defaults to runnable projects (not-inheritable && enabled)
    @Override
    public Predicate<RuntimeProject> getProjectFilter() {
        return super.getProjectFilter();
    }
}
