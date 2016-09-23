package com.inductiveautomation.ignition.examples;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.inductiveautomation.ignition.common.Dataset;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by mattgross on 9/15/2016. Methods in this interface can be called by clients and designers by using
 * ModuleRPCFactory.create()
 *
 */
public interface GetLogsRPC {

    /**
     * Returns a map of log entries from the specified remote servers. Each remote server is an entry in the map.
     * @param remoteServers
     * @param startDate returns logging events with a timestamp after this date. Set to null to not use a start date.
     * @param endDate returns logging events with a timestamp before this date. Set to null to not use an end date.
     * @return a map, where the key is a remote server, and the value is a List of the LoggingEvents from that server.
     */
    HashMap<String, List<LoggingEvent>> getRemoteLogEntries(List<String> remoteServers, Date startDate, Date endDate);
}
