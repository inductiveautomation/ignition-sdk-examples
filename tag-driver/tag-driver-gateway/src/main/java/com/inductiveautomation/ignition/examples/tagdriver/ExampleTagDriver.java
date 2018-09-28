package com.inductiveautomation.ignition.examples.tagdriver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.ExampleDeviceType;
import com.inductiveautomation.ignition.gateway.opcua.server.api.Device;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceSettingsRecord;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.api.AccessContext;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.eclipse.milo.opcua.stack.core.util.FutureUtils;
import org.jetbrains.annotations.NotNull;

public class ExampleTagDriver implements Device {

    private final DeviceContext deviceContext;
    private final DeviceSettingsRecord settings;
    private final OpcUaServer server;
    private final UaNodeManager nodeManager;
    private Set<DataItem> subscribedItems = new HashSet<>();
    private volatile int counterValue = 0;

    /**
     * Creates some tags that can be referenced when the driver is running.
     *  @param deviceContext
     * @param settings
     */
    public ExampleTagDriver(DeviceContext deviceContext, DeviceSettingsRecord settings) {
        this.deviceContext = deviceContext;
        this.settings = settings;
        this.server = deviceContext.getServer();
        this.nodeManager = deviceContext.getServer().getNodeManager();
    }

    @NotNull
    @Override
    public String getName() {
        return deviceContext.getName();
    }

    @NotNull
    @Override
    public String getStatus() {
        return "Good";
    }

    @NotNull
    @Override
    public String getTypeId() {
        return ExampleDeviceType.TYPE_ID;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void startup() {
        UaFolderNode deviceFolder = new UaFolderNode(
            server,
            deviceContext.nodeId(getName()),
            deviceContext.qualifiedName(getName()),
            new LocalizedText(getName())
        );
        addChildNode(deviceContext.getRootNodeId(), deviceFolder);

        UByte accessLevel = Unsigned.ubyte(AccessLevel.getMask(AccessLevel.READ_WRITE));
        UaNode variable = UaVariableNode.builder(server)
            .setNodeId(deviceContext.nodeId("test-id"))
            .setBrowseName(deviceContext.qualifiedName("displayName"))
            .setDisplayName(new LocalizedText("displayName"))
            .setDataType(BuiltinDataType.UInt16.getNodeId())
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .setAccessLevel(accessLevel)
            .setUserAccessLevel(accessLevel)
            .build();
        variable.setAttributeDelegate(new AttributeDelegate() {
            @Override
            public DataValue getValue(AttributeContext context, VariableNode node) {
                return new DataValue(new Variant(counterValue));
            }

            @Override
            public void setValue(AttributeContext context, VariableNode node, DataValue value) {
                counterValue = TypeUtilities.toInteger(value.getValue().getValue());
            }
        });

        addChildNode(deviceFolder.getNodeId(), variable);

        List<DataItem> dataItems = deviceContext.getSubscriptionModel().getDataItems(getName());

        onDataItemsCreated(dataItems);
    }

    private void addChildNode(NodeId parentId, UaNode child) {
        nodeManager.addNode(child);
        nodeManager.addReference(new Reference(
            child.getNodeId(),
            Identifiers.Organizes,
            parentId.expanded(),
            NodeClass.Object,
            false
        ));
    }

    @Override
    public void read(ReadContext readContext,
                     Double aDouble,
                     TimestampsToReturn timestampsToReturn,
                     List<ReadValueId> list) {
        List<DataValue> results = list.stream()
            .map((valueId) -> {
                UaNode node = server.getNodeManager().get(valueId.getNodeId());

                if (node != null) {
                    return node.readAttribute(
                        new AttributeContext(readContext),
                        valueId.getAttributeId(),
                        timestampsToReturn,
                        valueId.getIndexRange(),
                        valueId.getDataEncoding()
                    );
                } else {
                    return new DataValue(StatusCodes.Bad_NodeIdUnknown);
                }
            })
            .collect(Collectors.toList());

        readContext.complete(results);
    }

    @Override
    public void write(WriteContext writeContext, List<WriteValue> list) {
        List<StatusCode> results = list.stream()
            .map((valueId) -> {
                UaNode node = server.getNodeManager().get(valueId.getNodeId());

                if (node != null) {
                    try {
                        node.writeAttribute(
                            new AttributeContext(writeContext),
                            valueId.getAttributeId(),
                            valueId.getValue(),
                            valueId.getIndexRange()
                        );
                        return StatusCode.GOOD;
                    } catch (UaException e) {
                        return e.getStatusCode();
                    }
                } else {
                    return new StatusCode(StatusCodes.Bad_NodeIdUnknown);
                }
            })
            .collect(Collectors.toList());

        writeContext.complete(results);
    }

    @Override
    public void onDataItemsCreated(List<DataItem> list) {
        list.forEach(item -> {
            subscribedItems.add(item);
            item.setQuality(new StatusCode(StatusCodes.Uncertain_InitialValue));
        });
    }

    @Override
    public void onDataItemsModified(List<DataItem> list) {
        onDataItemsDeleted(list);
        onDataItemsCreated(list);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> list) {
        list.forEach(item -> subscribedItems.remove(item));
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> list) {
        //no-op
    }

    @Override
    public CompletableFuture<List<Reference>> browse(AccessContext accessContext, NodeId nodeId) {
        UaNode node = server.getNodeManager().get(nodeId);

        if (node != null) {
            return CompletableFuture.completedFuture(node.getReferences());
        } else {
            return FutureUtils.failedUaFuture(StatusCodes.Bad_NodeIdUnknown);
        }
    }
}
