package com.inductiveautomation.ignition.examples.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.script.hints.NoHint;
import com.inductiveautomation.ignition.examples.GetLogsRPC;
import com.inductiveautomation.ignition.examples.GetLogsScriptFunctions;

/**
 * Created by mattgross on 9/15/2016. Client implementation of GetLogsScriptFunctions. We need to make an RPC call
 * to the Gateway, which will perform the actual work of retrieving log entries from remote Gateways.
 */
public class GetLogsClientFunctions extends GetLogsScriptFunctions {

    @NoHint
    @Override
    public HashMap<String, List<LogEvent>> getLogEntriesInternal(List<String> remoteServers, Date startDate, Date endDate){
        GetLogsRPC rpc = ModuleRPCFactory.create("com.example.get-remote-logs", GetLogsRPC.class);
        HashMap<String, List<LogEvent>> map = rpc.getRemoteLogEntries(remoteServers, startDate, endDate);
        return map;
    }
}
