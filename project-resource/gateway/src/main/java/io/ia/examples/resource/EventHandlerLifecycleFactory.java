package io.ia.examples.resource;

import java.util.List;
import java.util.function.Predicate;
import com.inductiveautomation.ignition.common.resourcecollection.ResourceFilter;
import com.inductiveautomation.ignition.common.resourcecollection.RuntimeResourceCollection;
import com.inductiveautomation.ignition.common.script.ModuleLibrary;
import com.inductiveautomation.ignition.common.script.ScriptLibrary;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.resourcecollection.ResourceCollectionLifecycleFactory;

public class EventHandlerLifecycleFactory extends ResourceCollectionLifecycleFactory<EventHandlerLifecycle> {
    private final GatewayContext context;

    public EventHandlerLifecycleFactory(GatewayContext context) {
        super(context.getProjectManager());
        this.context = context;
    }

    @Override
    public EventHandlerLifecycle createLifecycle(RuntimeResourceCollection runtimeResourceCollection) {
        return new EventHandlerLifecycle(runtimeResourceCollection, context);
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
    public Predicate<RuntimeResourceCollection> getResourceCollectionFilter() {
        return super.getResourceCollectionFilter();
    }
}
