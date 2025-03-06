package com.inductiveautomation.ignition.examples.gn;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.rpc.RpcInterface;

/**
 * Methods in this interface can be called by clients and designers.
 *
 */
@RpcInterface(packageId = "getlogs-rpc")
public interface GetLogsFunctions {
    String MODULE_ID = "com.inductiveautomation.examples.gateway-network-function";

    /**
     * Returns a map of log entries from the specified remote servers. Each remote server is an entry in the map.
     * @param remoteServers A list of remote gateway names
     * @param startDate returns logging events with a timestamp after this date. Set to null to not use a start date.
     * @param endDate returns logging events with a timestamp before this date. Set to null to not use an end date.
     * @return a map, where the key is a remote server, and the value is a List of the LoggingEvents from that server.
     */
    Map<String, List<LogEvent>> getRemoteLogEntries(List<String> remoteServers, Date startDate, Date endDate);

    /**
     * Retrieves the actual system_logs.idb file from the remote gateway using the gateway network file transfer system.
     * @param remoteServer The remote gateway name
     * @return A byte array containing the log db file
     */
    byte[] getRemoteLogFile(String remoteServer) throws IOException;

    /**
     * Pushes the local system_logs.idb file to a remote gateway using the gateway network file transfer system.
     * @param remoteServer The gateway that should receive the log file
     * @return A message from the remote gateway reporting the size of the received file in bytes
     */
    String pushRemoteLogFile(String remoteServer) throws IOException;
}
