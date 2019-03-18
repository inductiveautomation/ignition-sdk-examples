package com.inductiveautomation.ignition.examples.tagdriver;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.ExampleDeviceType;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.ValueSimulator;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.settings.ExampleDeviceSettings;
import com.inductiveautomation.ignition.gateway.opcua.server.api.Device;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;
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
import org.eclipse.milo.opcua.sdk.server.nodes.UaServerNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.sdk.server.nodes.factories.NodeFactory;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
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

public class ExampleDevice implements Device {

    private final DeviceContext deviceContext;
    private final ExampleDeviceSettings settings;
    private final OpcUaServer server;
    private final SubscriptionModel subscriptionModel;
    private final UaNodeManager nodeManager;
    private final NodeFactory nodeFactory;
    private LoggerEx logger = LoggerEx.newBuilder().build(getClass().getSimpleName());
    private UByte READ_ONLY = Unsigned.ubyte(AccessLevel.getMask(AccessLevel.READ_ONLY));
    private UByte READ_WRITE = Unsigned.ubyte(AccessLevel.getMask(AccessLevel.READ_WRITE));

    public ExampleDevice(DeviceContext deviceContext, ExampleDeviceSettings settings) {
        this.deviceContext = deviceContext;
        this.settings = settings;
        this.server = deviceContext.getServer();
        this.nodeManager = deviceContext.getServer().getNodeManager();
        this.subscriptionModel = new SubscriptionModel(deviceContext.getServer(), this);
        this.nodeFactory = server.getNodeFactory();
    }

    @Override
    public String getName() {
        return deviceContext.getName();
    }

    @Override
    public String getStatus() {
        return "Good";
    }

    @Override
    public String getTypeId() {
        return ExampleDeviceType.TYPE_ID;
    }

    @Override
    public void shutdown() {
        deviceContext.getGatewayContext()
            .getExecutionManager()
            .unRegister(ExampleDeviceType.TYPE_ID, deviceContext.getName());
    }

    @Override
    public void startup() {
        // Create a folder node for our configured device
        UaFolderNode rootNode = new UaFolderNode(
            server,
            deviceContext.nodeId(getName()),
            deviceContext.qualifiedName(String.format("[%s]", getName())),
            new LocalizedText(String.format("[%s]", getName()))
        );
        // add the folder to the server
        nodeManager.addNode(rootNode);
        // base device context also needs to know about our folder
        nodeManager.addReference(new Reference(
            rootNode.getNodeId(),
            Identifiers.Organizes,
            deviceContext.getRootNodeId().expanded(),
            NodeClass.Object,
            false
        ));

        addSimpleFolder(rootNode, "static", settings.getTagCount(), READ_WRITE);
        addSimpleFolder(rootNode, "readOnly", settings.getTagCount(), READ_ONLY);

        addDynamicNodes(rootNode);

        //fire initial subscription creation
        List<DataItem> dataItems = deviceContext.getSubscriptionModel().getDataItems(getName());
        onDataItemsCreated(dataItems);
    }

    private void addDynamicNodes(UaFolderNode rootNode) {
        String name = "dynamic";
        UaFolderNode folder = new UaFolderNode(
            server,
            deviceContext.nodeId(name),
            deviceContext.qualifiedName(name),
            new LocalizedText(name)
        );
        nodeManager.addNode(folder);
        // addOrganizes is just a helper method to add references to/from folder nodes
        rootNode.addOrganizes(folder);

        // create a basic tag updater service, let the gateway run it separately from UA subscription management
        ValueSimulator simulator = new ValueSimulator();
        deviceContext.getGatewayContext()
            .getExecutionManager()
            .registerAtFixedRate(ExampleDeviceType.TYPE_ID, deviceContext.getName(), simulator, 1, TimeUnit.SECONDS);

        for (int i = 0; i < settings.getTagCount(); i++) {
            String formattedName = String.format("%s%d", name, i);
            UaVariableNode node = UaVariableNode.builder(server)
                .setNodeId(deviceContext.nodeId(String.format("%s/node%d", formattedName, i)))
                .setBrowseName(deviceContext.qualifiedName(formattedName))
                .setDisplayName(new LocalizedText(formattedName))
                .setDataType(BuiltinDataType.UInt32.getNodeId())
                .setTypeDefinition(Identifiers.BaseDataVariableType)
                .setAccessLevel(READ_ONLY)
                .setUserAccessLevel(READ_ONLY)
                .build();

            // just tells our simulator to keep track of this node
            simulator.addTrackedValue(formattedName, i);

            // an attribute delegate is used so that when this node is asked for its value, it will call out to the
            // simulator
            node.setAttributeDelegate(new AttributeDelegate() {
                @Override
                public DataValue getValue(AttributeContext context, VariableNode node) {
                    return simulator.getTrackedValue(formattedName);
                }
            });
            nodeManager.addNode(node);
            folder.addOrganizes(node);
        }
    }

    private void addSimpleFolder(UaFolderNode rootNode, String name, int count, UByte accessLevel) {
        UaFolderNode folder = new UaFolderNode(
            server,
            deviceContext.nodeId(name),
            deviceContext.qualifiedName(name),
            new LocalizedText(name)
        );
        nodeManager.addNode(folder);
        rootNode.addOrganizes(folder);

        String name1 = folder.getDisplayName().getText();
        for (int i = 0; i < count; i++) {
            String formattedName = String.format("%s%d", name1, i);
            UaVariableNode node = UaVariableNode.builder(server)
                .setNodeId(deviceContext.nodeId(String.format("%s/node%d", formattedName, i)))
                .setBrowseName(deviceContext.qualifiedName(formattedName))
                .setDisplayName(new LocalizedText(formattedName))
                .setDataType(BuiltinDataType.UInt16.getNodeId())
                .setTypeDefinition(Identifiers.BaseDataVariableType)
                .setAccessLevel(accessLevel)
                .setUserAccessLevel(accessLevel)
                .build();
            node.setValue(new DataValue(new Variant(i)));
            nodeManager.addNode(node);
            folder.addOrganizes(node);
        }
    }

    @Override
    public void read(ReadContext context,
                     Double maxAge,
                     TimestampsToReturn timestamps,
                     List<ReadValueId> readValueIds) {
        List<DataValue> results = Lists.newArrayListWithCapacity(readValueIds.size());

        for (ReadValueId readValueId : readValueIds) {
            UaServerNode node = server.getNodeManager().get(readValueId.getNodeId());

            if (node != null) {
                DataValue value = node.readAttribute(
                    new AttributeContext(context),
                    readValueId.getAttributeId(),
                    timestamps,
                    readValueId.getIndexRange(),
                    readValueId.getDataEncoding()
                );

                results.add(value);
            } else {
                results.add(new DataValue(StatusCodes.Bad_NodeIdUnknown));
            }
        }
        context.complete(results);
    }

    @Override
    public void write(WriteContext context, List<WriteValue> writeValues) {
        List<StatusCode> results = Lists.newArrayListWithCapacity(writeValues.size());

        for (WriteValue writeValue : writeValues) {
            UaServerNode node = server.getNodeManager().get(writeValue.getNodeId());

            if (node != null) {
                try {
                    node.writeAttribute(
                        new AttributeContext(context),
                        writeValue.getAttributeId(),
                        writeValue.getValue(),
                        writeValue.getIndexRange()
                    );

                    results.add(StatusCode.GOOD);

                    logger.infof(
                        "Wrote value {} to {} attribute of {}",
                        writeValue.getValue().getValue(),
                        AttributeId.from(writeValue.getAttributeId()).map(Object::toString).orElse("unknown"),
                        node.getNodeId()
                    );
                } catch (UaException e) {
                    logger.errorf("Unable to write value={}", writeValue.getValue(), e);
                    results.add(e.getStatusCode());
                }
            } else {
                results.add(new StatusCode(StatusCodes.Bad_NodeIdUnknown));
            }
        }
        context.complete(results);
    }

    @Override
    public void onDataItemsCreated(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
        subscriptionModel.onMonitoringModeChanged(monitoredItems);
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
