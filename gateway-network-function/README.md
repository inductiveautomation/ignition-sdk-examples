# Get Remote Logs (Gateway Network) Example
This module provides examples for the following functionality:
- retrieve log events for a remote server over the Gateway network in the form of a Python dataset. A start date and an end date can be used to filter log queries.
- download a copy of system_logs.idb file from a remote server. This functionality also demonstrates file streaming over the Gateway Network using a CompletableFuture to wait for the file to download.
- stream a local copy of system_logs.idb to a remote server. This functionality also demonstrates file streaming over the Gateway Network by passing a file path as a service call parameter.

## How to Use This Module
### Requirements
The following are required:
1. Two Gateway instances connected via the Gateway Area Network. 
2. This module must be installed on both Gateways.

> [!NOTE]
> `getRemoteLogEntries()` will work once the above requirements are met.  
> `getRemoteLogFile()` and `pushRemoteLogFile()` require an additional step: a Controller/Agent EAM configuration between the two Gateways must be created with the Controller being the issue of the script call and the Agent being the target of the script call.  

### Example Scripts
#### Retrieve Logs as Dataset
This retrieves log entries and places them in a dataset. The dataset is sorted in reverse chronological order, with the most recent entry being first in the dataset.
```python
import datetime

servers = ["agent"]
startTime = datetime.datetime.now() - datetime.timedelta(hours=3)

map = system.example.gn.getRemoteLogEntries(servers, startTime)
serverLogs = map["agent"]
for row in range(serverLogs.rowCount):
	printable = {
	'level': serverLogs.getValueAt(row, "level"), 
	'name': serverLogs.getValueAt(row, "name"),
	'timestamp': serverLogs.getValueAt(row, "timestamp"),
	'message': serverLogs.getValueAt(row, "message")
	}
	
	baseStr = '%(level)s [%(name)s] [%(timestamp)s]: %(message)s'
	print baseStr % printable
```

#### Retrieve Logs as File
After system_logs.idb is downloaded to your local machine, you can use [Kindling](https://github.com/inductiveautomation/kindling) to view the file.
```python
import datetime

remote_gateway = "agent"
bytes = system.example.gn.getRemoteLogFile(remote_gateway)

now=datetime.datetime.now().strftime("%m%d%Y_%H%M%S")
save_file="/tmp/%s_%s-system_logs.idb" % (remote_gateway, now)
print("Successfully downloaded system_logs.idb, saving to '%s'" % save_file)
system.file.writeFile(save_file, bytes)
```

#### Send Logs as File
This function is minimally useful as an example, but it does demonstrate how to stream a file over the gateway network as a service method parameter.
```python
print system.example.gn.pushRemoteLogFile("controller")
```

## Working with the Code
### Java class structure
`com.inductiveautomation.ignition.examples.gn.GetLogsFunctions` The interface for the gateway to designer/client RPC functions.

`com.inductiveautomation.ignition.examples.gn.AbstractGetLogsFunctions` The system.example.gn functions are executed here for both the gateway and designer/client. The functions call abstract internal functions that do different things depending on whether in gateway or designer/client scope.

`com.inductiveautomation.ignition.examples.gn.GetLogsGatewayFunctions` The actual work of calling remote gateways is performed in this class using GetLogsService calls. Methods in this class are also called by client/designer script functions through the RPC system.

`com.inductiveautomation.ignition.examples.client.gn.GetLogsClientFunctions` Calls the GetLogsFunctions RPC interface on behalf of client/designer script functions.

`com.inductiveautomation.ignition.examples.gn.service.GetLogsService` An interface that gets registered as the GetLogsFunctions gateway network service.

`com.inductiveautomation.ignition.examples.gn.service.GetLogsServiceImpl` The implementation of the GetLogsFunctions service interface. This class handles GetLogsFunctions remote service calls.

`gateway-network-function/gateway-network-function-common/src/main/proto/logevent.proto` This file provides the Protobuf message format for the LogEvents class.

`com.inductiveautomation.ignition.examples.gn.protoserializers.LogEventSerializer` Implements the ProtobufSerializable interface and provides the glue between the logevent.proto file and the gateway network. It converts a LogEvent object into a Protobuf message, and vice versa.

### Protobuf implementation
The Ignition gateway network doesn't have a built-in Protobuf serializer for the LogEvent class, so this example adds one. Without this, LogEvent objects can't be serialized and sent between gateways. The serializer is implemented like so:
- gateway-network-function/gateway-network-function-common/src/main/proto/logevent.proto
- gateway-network-function/gateway-network-function-common/src/main/java/com/inductiveautomation/ignition/examples/gn/protoserializers/LogEventSerializer.java

When running the `mvn package` task, the .proto file will be transpiled into a .java object. The .java object is later compiled into a Java class and is included in the module's common jar. When the module is installed, it will be able to use the LogEventSerializer to Protobuf serialize messages between gateways.

If you want to see the Protobuf serialization in action, access the Logs section on the gateways and set this logger to TRACE:
`metro.serializers.ProtobufEncoder.Direct`

If you call the `system.example.gn.getRemoteLogEntries()` script function, one of the log entries on one gateway will contain some Protobuf encoding information:
```
Encoding job 365 service response 'CallResult:GetLogsService/getLogEvents': header='{ "intentName": "_rpc:355|0", "codecName": "_svcres_", "headersValues": { "_headerid_": "e42a75f4-81bd-4430-9545-ebfc4713fd58", "_source_": "_0:0:agent", "_ver_": "2" } }', data='{ "body": { "@type": "type.googleapis.com/metro.protobuf.ServiceResponsePB", "result": { "@type": "type.googleapis.com/metro.protobuf.ListPB", "listType": "List_ArrayList", "items": [{ "@type": "type.googleapis.com/getlogs.protobuf.LogEventPB", "timestamp": "1741183729192",.....
```
