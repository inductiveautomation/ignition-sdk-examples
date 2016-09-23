package com.inductiveautomation.ignition.examples;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.script.hints.NoHint;

/**
 * Created by mattgross on 9/15/2016. Gateway implementation of GetLogsScriptFunctions. The actual code to retrieve
 * remote log entries is in the passed rpc object, so we just call that.
 */
public class GetLogsGatewayFunctions extends GetLogsScriptFunctions {

    GetLogsRPCImpl rpc;

    public GetLogsGatewayFunctions(GetLogsRPCImpl rpc){
        this.rpc = rpc;
    }

    @NoHint
    @Override
    public HashMap<String, List<LogEvent>> getLogEntriesInternal(List<String> remoteServers, Date startDate, Date endDate){
        return rpc.getRemoteLogEntries(remoteServers, startDate, endDate);
    }

}
