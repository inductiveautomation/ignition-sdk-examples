package io.ia.examples.resource;

import java.util.List;
import com.inductiveautomation.ignition.common.resourcecollection.Resource;
import com.inductiveautomation.ignition.common.resourcecollection.ResourceId;
import com.inductiveautomation.ignition.common.resourcecollection.RuntimeResourceCollection;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.resourcecollection.ResourceCollectionLifecycle;

/**
 * Override the methods on ResourceCollectionLifecycle to handle project updates performed in the designer.
 */
public class EventHandlerLifecycle extends ResourceCollectionLifecycle {
    private final GatewayContext context;

    public EventHandlerLifecycle(RuntimeResourceCollection project, GatewayContext context) {
        super(project);
        this.context = context;
    }

    @Override
    protected void onStartup(List<Resource> list) {
        for (Resource resource : list) {
            if (resource.getResourceType().equals(PythonResource.RESOURCE_TYPE)) {
                var pythonResource = PythonResource.fromResource(resource);
                // do something with the resource
            }
        }
    }

    @Override
    protected void onShutdown(List<ResourceId> list) {

    }

    @Override
    protected void onResourcesCreated(List<Resource> list) {

    }

    @Override
    protected void onResourcesModified(List<Resource> list) {

    }

    @Override
    protected void onResourcesDeleted(List<ResourceId> list) {

    }
}
