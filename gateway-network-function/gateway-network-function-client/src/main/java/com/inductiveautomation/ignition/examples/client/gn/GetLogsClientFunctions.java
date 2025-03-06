package com.inductiveautomation.ignition.examples.client.gn;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnection;
import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.script.hints.NoHint;
import com.inductiveautomation.ignition.examples.gn.AbstractGetLogsFunctions;
import com.inductiveautomation.ignition.examples.gn.GetLogsFunctions;

/**
 * This class makes gateway calls via its internal rpc object, which will perform the actual work of retrieving
 * log entries from remote Gateways.
 */
public class GetLogsClientFunctions extends AbstractGetLogsFunctions {
    private final GetLogsFunctions rpc;

    public GetLogsClientFunctions() {
        // This sets up the designer/client side of the RPC implementation for this module. The gateway side is set
        // up in GatewayHook#setup(). The rpc object is called whenever a message needs to be sent from the gateway.
        rpc = GatewayConnection.getRpcInterface(
            this.rpcSerializer,
            GetLogsFunctions.MODULE_ID,
            GetLogsFunctions.class);
    }

    @NoHint
    @Override
    public Map<String, List<LogEvent>> getLogEntriesInternal(List<String> remoteServers,
                                                             Date startDate,
                                                             Date endDate) {
        return rpc.getRemoteLogEntries(remoteServers, startDate, endDate);
    }

    @NoHint
    @Override
    public byte[] getLogFileInternal(String remoteServer) throws IOException {
        return rpc.getRemoteLogFile(remoteServer);
    }

    @NoHint
    @Override
    public String pushLogFileInternal(String remoteServer) throws IOException {
        return rpc.pushRemoteLogFile(remoteServer);
    }
}
