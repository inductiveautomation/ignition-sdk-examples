/*******************************************************************************
 * INDUCTIVE AUTOMATION PUBLIC LICENSE 
 *
 * BY DOWNLOADING, INSTALLING AND/OR IMPLEMENTING THIS SOFTWARE YOU AGREE 
 * TO THE FOLLOWING LICENSE: 
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are 
 * met: 
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions in 
 * binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. Neither the name of 
 * Inductive Automation nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS 
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INDUCTIVE 
 * AUTOMATION BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
 * LICENSEE SHALL INDEMNIFY, DEFEND AND HOLD HARMLESS INDUCTIVE AUTOMATION, 
 * ITS SHAREHOLDERS, OFFICERS, DIRECTORS, EMPLOYEES, AGENTS, ATTORNEYS, 
 * SUCCESSORS AND ASSIGNS FROM ANY AND ALL claims, debts, liabilities, 
 * demands, suits and causes of action, known or unknown, in any way 
 * relating to the LICENSEE'S USE OF THE SOFTWARE IN ANY FORM OR MANNER
 * WHATSOEVER AND FOR any act or omission related thereto.
 ******************************************************************************/
package com.inductiveautomation.xopc.drivers.modbus2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.inductiveautomation.opcua.nodes.Node;
import com.inductiveautomation.opcua.types.AccessLevel;
import com.inductiveautomation.opcua.types.DataType;
import com.inductiveautomation.opcua.types.DataValue;
import com.inductiveautomation.opcua.types.LocalizedText;
import com.inductiveautomation.opcua.types.NodeId;
import com.inductiveautomation.opcua.types.QualifiedName;
import com.inductiveautomation.opcua.types.StatusCode;
import com.inductiveautomation.opcua.util.NodeIds;
import com.inductiveautomation.xopc.driver.api.AbstractIODelegatingDriver;
import com.inductiveautomation.xopc.driver.api.BrowseOperation;
import com.inductiveautomation.xopc.driver.api.DriverContext;
import com.inductiveautomation.xopc.driver.api.DriverState;
import com.inductiveautomation.xopc.driver.api.browsing.BrowseNode;
import com.inductiveautomation.xopc.driver.api.browsing.DataVariableNode;
import com.inductiveautomation.xopc.driver.api.browsing.FolderNode;
import com.inductiveautomation.xopc.driver.api.items.DriverItem;
import com.inductiveautomation.xopc.driver.api.items.ReadItem;
import com.inductiveautomation.xopc.driver.api.items.WriteItem;
import com.inductiveautomation.xopc.driver.api.requests.Request;
import com.inductiveautomation.xopc.driver.util.AddressNotFoundException;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusAddress;
import com.inductiveautomation.xopc.drivers.modbus2.address.ModbusTable;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.AddressType;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.DesignatorRange;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.ModbusAddressMap;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.ModbusRange;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.MutableModbusAddressMap;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.ModbusDriverSettings;
import com.inductiveautomation.xopc.drivers.modbus2.factories.ModbusTransportFactory;
import com.inductiveautomation.xopc.drivers.modbus2.factories.ReadRequestFactory;
import com.inductiveautomation.xopc.drivers.modbus2.factories.WriteRequestFactory;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.ModbusReadOptimizer;
import com.inductiveautomation.xopc.drivers.modbus2.requests.optimizer.ModbusWriteOptimizer;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.ChannelWriter;
import com.inductiveautomation.xopc.drivers.modbus2.requests.util.CommunicationCallback;
import com.inductiveautomation.xopc.drivers.modbus2.util.AddressResolver;
import org.apache.log4j.Logger;

public abstract class AbstractModbusDriver extends AbstractIODelegatingDriver {

//	private int communicationTimeout = 2000;
//	private boolean reconnectAfterConsecutiveTimeouts = true;
//
//	private int maxCoilsPerRequest = 2000;
//	private int maxDiscreteInputsPerRequest = 2000;
//	private int maxHoldingRegistersPerRequest = 125;
//	private int maxInputRegistersPerRequest = 125;
//
//	private boolean spanGaps = true;
//	private boolean zeroBasedAddressing = false;
//	private boolean reverseWordOrder = false;
//	private boolean reverseStringByteOrder = false;
//	private boolean rightJustifyStrings = false;
//
//	private boolean writeMultipleCoilsRequestAllowed = true;
//	private boolean writeMultipleRegistersRequestAllowed = true;
//	private boolean readMultipleRegistersRequestAllowed = true;
//	private boolean readMultipleCoilsAllowed = true;
//	private boolean readMultipleDiscreteInputsAllowed = true;

	private final AddressResolver addressResolver = new ModbusAddressResolver();

	private final List<Node> uaNodes = new ArrayList<Node>();
	private final Map<String, BrowseNode> nodeMap = new HashMap<String, BrowseNode>();
	private final Map<String, String> mappedAddresses = new HashMap<String, String>();

	private static final String ROOT_NODE_ADDRESS = "ROOT";
	private final FolderNode rootNode = new FolderNode(ROOT_NODE_ADDRESS);

	private ReadRequestFactory readFactory;
	private WriteRequestFactory writeFactory;

	private ModbusReadOptimizer readOptimizer;
	private ModbusWriteOptimizer writeOptimizer;

	protected AbstractModbusDriver(final DriverContext driverContext, final ModbusDriverSettings settings) {
		super(driverContext);

		setAddressMap(settings.getAddressMap());

		ChannelWriter channelWriter = new ChannelWriter() {
			@Override
			public void writeToChannel(ByteBuffer... buffers) {
				AbstractModbusDriver.this.writeToChannel(buffers);
			}
		};

		CommunicationCallback communicationCallback = new CommunicationCallback() {
			private final AtomicInteger timeouts = new AtomicInteger();

			@Override
			public void notifyCommunicationTimeout() {
				if (AbstractModbusDriver.this.getDriverState() != DriverState.Connected) {
					timeouts.set(0);
					return;
				}

				if (timeouts.incrementAndGet() > 3 && settings.isReconnectAfterConsecutiveTimeouts()) {
					timeouts.set(0);
					reconnect();
				}
			}

			@Override
			public void notifyCommunicationSuccess() {
				timeouts.set(0);
			}
		};

		ModbusTransportFactory transportFactory = getTransportFactory();

		readFactory = new ReadRequestFactory(
				channelWriter,
				transportFactory,
				settings.isZeroBasedAddressing(),
				settings.getCommunicationTimeout(),
				log,
				settings.isReverseWordOrder(),
				settings.isReverseStringByteOrder(),
				communicationCallback);

		writeFactory = new WriteRequestFactory(
				channelWriter,
				transportFactory,
				settings.isZeroBasedAddressing(),
				settings.getCommunicationTimeout(),
				log,
				settings.isReverseWordOrder(),
				settings.isRightJustifyStrings(),
				settings.isReverseStringByteOrder(),
				communicationCallback);

		readOptimizer = new ModbusReadOptimizer(
				settings.getMaxCoilsPerRequest(),
				settings.getMaxDiscreteInputsPerRequest(),
				settings.getMaxHoldingRegistersPerRequest(),
				settings.getMaxInputRegistersPerRequest(),
				settings.isSpanGaps(),
				settings.isReadMultipleRegistersRequestAllowed(),
				settings.isReadMultipleCoilsAllowed(),
				settings.isReadMultipleDiscreteInputsAllowed(),
				makeSubLogger(ModbusReadOptimizer.class.getSimpleName()));

		writeOptimizer = new ModbusWriteOptimizer(
				settings.isWriteMultipleCoilsRequestAllowed(),
				settings.isWriteMultipleRegistersRequestAllowed(),
				makeSubLogger(ModbusWriteOptimizer.class.getSimpleName()));

		nodeMap.put(ROOT_NODE_ADDRESS, rootNode);
	}

	protected abstract ModbusTransportFactory getTransportFactory();

	@Override
	public void shutdown() {
		super.shutdown();

		for (Node node : uaNodes) {
			nodeManager.removeNode(node);
		}

		uaNodes.clear();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected List<List<? extends ReadItem>> optimizeRead(List<? extends ReadItem> items) {
		setModbusAddressObject(items);

		List<List<? extends ReadItem>> optimized = new ArrayList<List<? extends ReadItem>>();
		optimized.addAll(readOptimizer.optimizeReads((List<ReadItem>) items));

		return optimized;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected List<List<? extends WriteItem>> optimizeWrite(List<? extends WriteItem> items) {
		setModbusAddressObject(items);

		List<List<? extends WriteItem>> optimized = new ArrayList<List<? extends WriteItem>>();
		optimized.addAll(writeOptimizer.optimizeWrites((List<WriteItem>) items));

		return optimized;
	}

	private void setModbusAddressObject(List<? extends DriverItem> items) {
		for (DriverItem item : items) {
			if (!(item.getAddressObject() instanceof ModbusAddress)) {
				String address = item.getAddress();
				ModbusAddress addressObj = addressResolver.resolve(address);

				if (addressObj == null) {
					throw new RuntimeException(String.format("Couldn't parse ModbusAddress from \"%s\".", address));
				}

				item.setAddressObject(addressObj);
			}
		}
	}

	@Override
	protected Request<byte[]> createConnectRequest() {
		// TODO Add a property that lets the user specify an address to read as a "connect" request.
		// Successful read == connected.

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Request<byte[]> createReadRequest(List<? extends ReadItem> items) {
		return readFactory.get((List<ReadItem>) items);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Request<byte[]> createWriteRequest(List<? extends WriteItem> items) {
		return writeFactory.get((List<WriteItem>) items);
	}

	@Override
	protected Request<byte[]> createBrowseRequest(BrowseOperation browseOp) {
		List<String> results = new ArrayList<String>();

		String address = browseOp.getStartingAddress();
		if (address == null || address.isEmpty()) {
			address = ROOT_NODE_ADDRESS;
		}
		BrowseNode node = nodeMap.get(address);

		if (node != null) {
			Iterator<BrowseNode> iter = node.getChildren();
			while (iter.hasNext()) {
				results.add(iter.next().getAddress());
			}
		}

		browseOp.browseDone(StatusCode.GOOD, results, currentGuid());

		return null;
	}

	@Override
	public void buildNode(String address, NodeId nodeId) throws AddressNotFoundException {
		ModbusAddress modbusAddress = addressResolver.resolve(address);

		if (modbusAddress == null) {
			BrowseNode node = nodeMap.get(address);
			if (node instanceof FolderNode) {
				Node uaNode = builderFactory.newObjectNodeBuilder()
											.setNodeId(nodeId)
											.setBrowseName(new QualifiedName(1, node.getDisplayName()))
											.setDisplayName(new LocalizedText(node.getDisplayName()))
											.setTypeDefinition(NodeIds.FolderType_ObjectType.getNodeId())
											.buildAndAdd(nodeManager);

				uaNodes.add(uaNode);
				return;
			} else {
				throw new AddressNotFoundException(String.format("Address \"%s\" not found.", address));
			}
		}

		BrowseNode node = nodeMap.get(address);
		if (node == null) {
			node = new DataVariableNode(
					address,
					address,
					new DataValue(StatusCode.BAD),
					modbusAddress.getUADataType());
			nodeMap.put(address, node);
		}

		if (node instanceof DataVariableNode) {
			DataType dataType = modbusAddress.getUADataType();

			Node uaNode = builderFactory.newVariableNodeBuilder()
										.setNodeId(nodeId)
										.setBrowseName(new QualifiedName(1, node.getDisplayName()))
										.setDisplayName(new LocalizedText(node.getDisplayName()))
										.setDataType(dataType.getNodeId())
										.setTypeDefinition(NodeIds.VariableNode_DataType.getNodeId())
										.setAccessLevel(getAccessLevel(modbusAddress))
										.setUserAccessLevel(getAccessLevel(modbusAddress))
										.buildAndAdd(nodeManager);

			uaNodes.add(uaNode);
		} else {
			throw new AddressNotFoundException(String.format("Could not build node for \"%s\".", address));
		}
	}

	private EnumSet<AccessLevel> getAccessLevel(ModbusAddress address) {
		EnumSet<AccessLevel> accessLevel = EnumSet.of(AccessLevel.CurrentRead);

		ModbusTable table = address.getTable();
		if (table == ModbusTable.HoldingRegisters || table == ModbusTable.Coils) {
			accessLevel.add(AccessLevel.CurrentWrite);
		}

		return accessLevel;
	}

	@Override
	protected boolean isBrowsingSupported() {
		return true;
	}

	@Override
	protected boolean isOfflineBrowsingSupported() {
		return true;
	}

	private Logger makeSubLogger(String name) {
		return Logger.getLogger(String.format("%s.%s", log.getName(), name));
	}

	private void setAddressMap(String addressMap) {
		ModbusAddressMap map = MutableModbusAddressMap.fromParseableString(addressMap);

		if (map == null) {
			return;
		}

		for (Map.Entry<DesignatorRange, ModbusRange> entry : map.entrySet()) {
			DesignatorRange designatorRange = entry.getKey();
			ModbusRange modbusRange = entry.getValue();

			int radix = map.getDesignatorRadix();
			boolean step = designatorRange.getStep();
			String dString = designatorRange.getDesignator();
			int dStart = Integer.parseInt(designatorRange.getStart(), radix);
			int dEnd = Integer.parseInt(designatorRange.getEnd(), radix);
			int modbusStart = Integer.parseInt(modbusRange.getStart());
			int unitId = Integer.parseInt(modbusRange.getUnitID());
			AddressType type = modbusRange.getModbusAddressType();

			String unitIdFolderAddress = "UnitId " + unitId;
			FolderNode unitIdFolder = (FolderNode) nodeMap.get(unitIdFolderAddress);
			if (unitIdFolder == null) {
				unitIdFolder = new FolderNode("UnitId " + unitId);
				rootNode.addChild(unitIdFolder);
				nodeMap.put(unitIdFolderAddress, unitIdFolder);
			}

			String folderName = unitId > 0 ?
					String.format("%s.%s%s-%s.%s%s", unitId, dString, Integer.toString(dStart, radix),
								  unitId, dString, Integer.toString(dEnd, radix)) :
					String.format("%s%s-%s%s", dString, Integer.toString(dStart, radix),
								  dString, Integer.toString(dEnd, radix));

			FolderNode folderNode = new FolderNode(folderName);
			unitIdFolder.addChild(folderNode);
			nodeMap.put(folderName, folderNode);

			int dNumber = dStart;
			int modbus = modbusStart;
			while (dNumber <= dEnd) {
				String nodeName = unitId > 0 ?
						String.format("%s.%s%s", unitId, dString, Integer.toString(dNumber, radix)) :
						String.format("%s%s", dString, Integer.toString(dNumber, radix));
				String nodeAddress = unitId > 0 ?
						String.format("%s.%s%s", unitId, type.getShortString(), modbus) :
						String.format("%s%s", type.getShortString(), modbus);

				ModbusAddress address = ModbusAddress.parse(nodeAddress);

				if (address == null) {
					Logger.getLogger("ModbusDriver2.SetAddressMap").warn(
							String.format("Couldn't parse %s in while creating address map.", nodeAddress));
					continue;
				}

				DataVariableNode node = new DataVariableNode(
						nodeName,
						nodeName,
						new DataValue(StatusCode.BAD_STALE),
						address.getUADataType());

				folderNode.addChild(node);
				nodeMap.put(nodeName, node);
				mappedAddresses.put(nodeName, nodeAddress);

				dNumber += (step ? address.getAddressSpan() : 1);
				modbus += address.getAddressSpan();
			}
		}
	}

	private class ModbusAddressResolver implements AddressResolver {
		@Override
		public ModbusAddress resolve(String address) {
			if (mappedAddresses.containsKey(address)) {
				return ModbusAddress.parse(mappedAddresses.get(address));
			} else {
				String unitIdPart = "";
				String addressPart;
				String bitPart = "";

				String[] ss = address.split("\\.");

				if (ss.length == 2) {
					if (isNumber(ss[0])) {
						unitIdPart = ss[0];
						addressPart = ss[1];
					} else {
						addressPart = ss[0];
						bitPart = ss[1];
					}
				} else if (ss.length == 3) {
					unitIdPart = ss[0];
					addressPart = ss[1];
					bitPart = ss[2];
				} else {
					return ModbusAddress.parse(address);
				}

				if (mappedAddresses.containsKey(addressPart)) {
					String actualAddress = mappedAddresses.get(addressPart);

					if (unitIdPart.length() > 0) {
						actualAddress = String.format("%s.%s", unitIdPart, actualAddress);
					}
					if (bitPart.length() > 0) {
						actualAddress = String.format("%s.%s", actualAddress, bitPart);
					}

					return ModbusAddress.parse(actualAddress);
				}

				// The key part of the mappedAddresses map contains unitId.Address for non-default
				// unit ids.
				String unitIdAndAddress = String.format("%s.%s", unitIdPart, addressPart);
				if (!unitIdPart.isEmpty() && mappedAddresses.containsKey(unitIdAndAddress)) {
					String actualAddress = mappedAddresses.get(unitIdAndAddress);

					if (unitIdPart.length() > 0 && !actualAddress.startsWith(unitIdPart)) {
						actualAddress = String.format("%s.%s", unitIdPart, actualAddress);
					}
					if (bitPart.length() > 0) {
						actualAddress = String.format("%s.%s", actualAddress, bitPart);
					}

					return ModbusAddress.parse(actualAddress);
				}
			}

			return ModbusAddress.parse(address);
		}

		private boolean isNumber(String s) {
			try {
				Integer.parseInt(s);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}

}
