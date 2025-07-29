package com.inductiveautomation.ignition.examples.tagdriver;

import com.inductiveautomation.ignition.gateway.opcua.server.api.Device;
import com.inductiveautomation.ignition.gateway.opcua.server.api.DeviceContext;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.ManagedAddressSpaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.items.DataItem;
import org.eclipse.milo.opcua.sdk.server.items.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilters;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.OpcUaDataType;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleDevice extends ManagedAddressSpaceWithLifecycle implements Device {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ValueSimulator simulator = new ValueSimulator();
  private final SubscriptionModel subscriptionModel;

  private final DeviceContext context;
  private final ExampleDeviceConfig config;

  public ExampleDevice(DeviceContext context, ExampleDeviceConfig config) {
    super(context.getServer());

    this.context = context;
    this.config = config;

    subscriptionModel = new SubscriptionModel(context.getServer(), this);

    getLifecycleManager().addLifecycle(subscriptionModel);
    getLifecycleManager().addStartupTask(this::onStartup);
    getLifecycleManager().addShutdownTask(this::onShutdown);
  }

  @Override
  public String getStatus() {
    return "Running";
  }

  private void onStartup() {
    // create a basic tag updater service, let the gateway run it separately from UA subscription
    // management
    context.getGatewayContext().getExecutionManager().registerAtFixedRate(
        ExampleDeviceExtensionPoint.TYPE_ID,
        context.getName(),
        simulator,
        1, TimeUnit.SECONDS
    );

    // create a folder node for our configured device
    var rootNode = new UaFolderNode(
        getNodeContext(),
        context.nodeId(context.getName()),
        context.qualifiedName(String.format("[%s]", context.getName())),
        new LocalizedText(String.format("[%s]", context.getName()))
    );

    // add the folder node to the server
    getNodeManager().addNode(rootNode);

    // add a reference to the root "Devices" folder node
    rootNode.addReference(new Reference(
        rootNode.getNodeId(),
        NodeIds.Organizes,
        context.getRootNodeId().expanded(),
        Reference.Direction.INVERSE
    ));

    addDynamicNodes(rootNode);

    addStaticNodes(rootNode, "static", config.general().tagCount(), AccessLevel.READ_WRITE);
    addStaticNodes(rootNode, "readOnly", config.general().tagCount(), AccessLevel.READ_ONLY);

    // fire initial subscription creation
    onDataItemsCreated(
        context.getSubscriptionModel()
            .getDataItems(context.getName())
    );
  }

  private void onShutdown() {
    context.getSubscriptionModel()
        .getDataItems(context.getName())
        .forEach(item -> item.setQuality(new StatusCode(StatusCodes.Uncertain_LastUsableValue)));

    context.getGatewayContext()
        .getExecutionManager()
        .unRegister(ExampleDeviceExtensionPoint.TYPE_ID, context.getName());
  }

  private void addDynamicNodes(UaFolderNode rootNode) {
    String name = "dynamic";
    UaFolderNode folder = new UaFolderNode(
        getNodeContext(),
        context.nodeId(name),
        context.qualifiedName(name),
        new LocalizedText(name)
    );
    getNodeManager().addNode(folder);

    // addOrganizes is just a helper method to an OPC UA "Organizes" references to a folder node
    rootNode.addOrganizes(folder);

    for (int i = 0; i < config.general().tagCount(); i++) {
      final int n = i;
      String formattedName = String.format("%s%d", name, n);

      UaVariableNode node = UaVariableNode.build(getNodeContext(), b ->
          b.setNodeId(context.nodeId(String.format("%s/node%d", formattedName, n)))
              .setBrowseName(context.qualifiedName(formattedName))
              .setDisplayName(new LocalizedText(formattedName))
              .setDataType(OpcUaDataType.UInt32.getNodeId())
              .setTypeDefinition(NodeIds.BaseDataVariableType)
              .setAccessLevel(AccessLevel.READ_ONLY)
              .setUserAccessLevel(AccessLevel.READ_ONLY)
              .build()
      );

      // just tells our simulator to keep track of this node
      simulator.addTrackedValue(formattedName, (long) i);

      // an AttributeFilter is used so that when this node is asked for its value, it will call out to the
      // simulator
      node.getFilterChain().addLast(AttributeFilters.getValue(
          getAttributeContext ->
              simulator.getTrackedValue(formattedName))
      );

      getNodeManager().addNode(node);
      folder.addOrganizes(node);
    }
  }

  private void addStaticNodes(
      UaFolderNode rootNode,
      String name,
      int count,
      Set<AccessLevel> accessLevel
  ) {

    UaFolderNode folder = new UaFolderNode(
        getNodeContext(),
        context.nodeId(name),
        context.qualifiedName(name),
        new LocalizedText(name)
    );
    getNodeManager().addNode(folder);
    rootNode.addOrganizes(folder);

    String name1 = folder.getDisplayName().getText();
    for (int i = 0; i < count; i++) {
      final int n = i;
      String formattedName = String.format("%s%d", name1, n);

      UaVariableNode node = UaVariableNode.build(getNodeContext(), b ->
          b.setNodeId(context.nodeId(String.format("%s/node%d", formattedName, n)))
              .setBrowseName(context.qualifiedName(formattedName))
              .setDisplayName(new LocalizedText(formattedName))
              .setDataType(OpcUaDataType.UInt16.getNodeId())
              .setTypeDefinition(NodeIds.BaseDataVariableType)
              .setAccessLevel(accessLevel)
              .setUserAccessLevel(accessLevel)
              .build()
      );

      node.setValue(new DataValue(new Variant(i)));

      if (accessLevel.contains(AccessLevel.CurrentWrite)) {
        // This filter just intercepts writes to log before
        // passing it to the next filter in the chain. The default
        // filter instance at the end will write the attribute to
        // the UaNode instance.
        node.getFilterChain().addLast(AttributeFilters.setValue((ctx, value) -> {
          logger.info("setValue: {}", value.getValue().getValue());

          ctx.setAttribute(AttributeId.Value, value);
        }));
      }

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
