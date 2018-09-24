# Get Remote Logs (Gateway Network) Example

This module provides examples for the following functionality:
- retrieve log events for a remote server over the Gateway network in the form of a Python dataset. A start date and an end date can be used to filter log queries.
- execute a Gateway task that can download a full wrapper.log file from a remote server. This functionality also demonstrates file streaming over the Gateway Network.

You will need to install the module on two separate Gateways. You can create a script to retrieve log entries from a remote machine, as shown below:

### Example Script

```
import datetime

servers = ["ide_controller"]
startTime = datetime.datetime.now() - datetime.timedelta(hours=3)

map = system.example.getRemoteLogEntries(servers, startTime)
serverLogs = map["ide_controller"]
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


To create a Gateway task to retrieve a wrapper.log, log into one of the Gateways and navigate to Config -> Gateway Tasks. Click on Create new Gateway Task and select the "Retrieve Wrapper Log" task.
