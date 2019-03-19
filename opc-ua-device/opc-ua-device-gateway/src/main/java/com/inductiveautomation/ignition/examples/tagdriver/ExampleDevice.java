package com.inductiveautomation.ignition.examples.tagdriver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

import com.inductiveautomation.ignition.examples.tagdriver.configuration.ExampleDeviceType;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.ValueSimulator;
import com.inductiveautomation.ignition.examples.tagdriver.configuration.settings.ExampleDeviceSettings;
import com.inductiveautomation.ignition.gateway.opcua.server.api.Device;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedAddressSpaceServices;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;

public class ExampleDevice extends ManagedAddressSpaceServices implements Device {

    private static final UByte READ_ONLY = Unsigned.ubyte(AccessLevel.getMask(AccessLevel.READ_ONLY));
    private static final UByte READ_WRITE = Unsigned.ubyte(AccessLevel.getMask(AccessLevel.READ_WRITE));

    private final SubscriptionModel subscriptionModel;

    private final DeviceContext deviceContext;
    private final ExampleDeviceSettings settings;

    public ExampleDevice(DeviceContext deviceContext, ExampleDeviceSettings settings) {
        super(deviceContext.getServer());

        this.deviceContext = deviceContext;
        this.settings = settings;

        subscriptionModel = new SubscriptionModel(deviceContext.getServer(), this);
    }

    @Nonnull
    @Override
    public String getName() {
        return deviceContext.getName();
    }

    @Nonnull
    @Override
    public String getStatus() {
        return "Running";
    }

    @Nonnull
    @Override
    public String getTypeId() {
        return ExampleDeviceType.TYPE_ID;
    }

    @Override
    public void onStartup() {
        super.onStartup();

        // create a folder node for our configured device
        UaFolderNode rootNode = new UaFolderNode(
            getNodeContext(),
            deviceContext.nodeId(getName()),
            deviceContext.qualifiedName(String.format("[%s]", getName())),
            new LocalizedText(String.format("[%s]", getName()))
        );

        // add the folder node to the server
        getNodeManager().addNode(rootNode);

        // add a reference to the root "Devices" folder node
        rootNode.addReference(new Reference(
            rootNode.getNodeId(),
            Identifiers.Organizes,
            deviceContext.getRootNodeId().expanded(),
            Reference.Direction.INVERSE
        ));

        addSimpleFolder(rootNode, "static", settings.getTagCount(), READ_WRITE);
        addSimpleFolder(rootNode, "readOnly", settings.getTagCount(), READ_ONLY);

        addDynamicNodes(rootNode);

        // fire initial subscription creation
        List<DataItem> dataItems = deviceContext.getSubscriptionModel().getDataItems(getName());
        onDataItemsCreated(dataItems);
    }

    @Override
    public void onShutdown() {
        super.getNodeContext();

        deviceContext.getGatewayContext()
            .getExecutionManager()
            .unRegister(ExampleDeviceType.TYPE_ID, deviceContext.getName());
    }

    private void addDynamicNodes(UaFolderNode rootNode) {
        String name = "dynamic";
        UaFolderNode folder = new UaFolderNode(
            getNodeContext(),
            deviceContext.nodeId(name),
            deviceContext.qualifiedName(name),
            new LocalizedText(name)
        );
        getNodeManager().addNode(folder);

        // addOrganizes is just a helper method to an OPC UA "Organizes" references to a folder node
        rootNode.addOrganizes(folder);

        // create a basic tag updater service, let the gateway run it separately from UA subscription management
        ValueSimulator simulator = new ValueSimulator();
        deviceContext.getGatewayContext()
            .getExecutionManager()
            .registerAtFixedRate(ExampleDeviceType.TYPE_ID, deviceContext.getName(), simulator, 1, TimeUnit.SECONDS);

        for (int i = 0; i < settings.getTagCount(); i++) {
            String formattedName = String.format("%s%d", name, i);
            UaVariableNode node = UaVariableNode.builder(getNodeContext())
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
            getNodeManager().addNode(node);
            folder.addOrganizes(node);
        }
    }

    private void addSimpleFolder(UaFolderNode rootNode, String name, int count, UByte accessLevel) {
        UaFolderNode folder = new UaFolderNode(
            getNodeContext(),
            deviceContext.nodeId(name),
            deviceContext.qualifiedName(name),
            new LocalizedText(name)
        );
        getNodeManager().addNode(folder);
        rootNode.addOrganizes(folder);

        String name1 = folder.getDisplayName().getText();
        for (int i = 0; i < count; i++) {
            String formattedName = String.format("%s%d", name1, i);
            UaVariableNode node = UaVariableNode.builder(getNodeContext())
                .setNodeId(deviceContext.nodeId(String.format("%s/node%d", formattedName, i)))
                .setBrowseName(deviceContext.qualifiedName(formattedName))
                .setDisplayName(new LocalizedText(formattedName))
                .setDataType(BuiltinDataType.UInt16.getNodeId())
                .setTypeDefinition(Identifiers.BaseDataVariableType)
                .setAccessLevel(accessLevel)
                .setUserAccessLevel(accessLevel)
                .build();
            node.setValue(new DataValue(new Variant(i)));
            getNodeManager().addNode(node);
            folder.addOrganizes(node);
        }
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

}
