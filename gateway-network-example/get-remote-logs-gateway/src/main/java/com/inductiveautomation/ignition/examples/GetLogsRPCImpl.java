package com.inductiveautomation.ignition.examples;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.examples.service.GetLogsService;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.api.ServiceManager;
import com.inductiveautomation.metro.api.services.ServiceState;
import org.apache.log4j.Logger;

/**
 * Created by mattgross on 9/15/2016. Implementation of GetLogsRPC, and handles remote calls from clients and designers.
 */
public class GetLogsRPCImpl implements GetLogsRPC {

    private GatewayContext context;
    private Logger logger = Logger.getLogger("GetLogsRPC");

    public GetLogsRPCImpl(GatewayContext context){
        this.context = context;
    }

    @Override
    public HashMap<String, List<LogEvent>> getRemoteLogEntries(List<String> remoteServers, Date startDate, Date endDate){

        HashMap<String, List<LogEvent>> logsMap = new HashMap<>();

        ServiceManager sm = context.getGatewayAreaNetworkManager().getServiceManager();
        for(String server: remoteServers){
            ServerId serverId = ServerId.fromString(server);

            // First, verify that the service is available on the remote machine before trying to call.
            ServiceState state = sm.getRemoteServiceState(serverId, GetLogsService.class);
            if(state != ServiceState.Available){
                logger.error(String.format("Service was unavailable for server '%s', current state is %s",
                        serverId.toDescriptiveString(),
                        state.toString()));
            }
            else{
                // The service call will time out after 60 seconds if no response is received from the remote Gateway.
                List<LogEvent> events = sm.getService(serverId, GetLogsService.class).get().getLogEvents(startDate, endDate);
                logsMap.put(server, events);
            }
        }

        return logsMap;
    }
}
