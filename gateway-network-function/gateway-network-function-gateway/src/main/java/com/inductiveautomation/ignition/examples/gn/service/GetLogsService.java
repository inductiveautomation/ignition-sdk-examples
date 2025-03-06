package com.inductiveautomation.ignition.examples.gn.service;

import java.util.Date;
import java.util.List;

import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.gateway.gan.security.TrialPeriodProtected;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.api.services.ServiceDescription;
import com.inductiveautomation.metro.impl.services.annotations.FileStream;

/**
 * Interface for the logs gateway network functions. This interface and its implementation are needed for the gateway
 * network service system.
 */
@TrialPeriodProtected(moduleId = "com.inductiveautomation.examples.gateway-network-function")
@ServiceDescription(name = "Gateway Network Demo",
    description = "Demonstrates how to transfer log entries and files over the gateway network")
public interface GetLogsService {
    String SUCCESS_MSG = "SUCCESS";
    String FAIL_MSG = "FAIL";

    /**
     * Provides a List of LoggingEvents to the calling machine. An empty List is returned if no logging events are
     * available.
     * @param startDate returns logging events with a timestamp after this date. Set to null to not use a start date.
     * @param endDate returns logging events with a timestamp before this date. Set to null to not use an end date.
     * @return a List of LoggingEvents, or an empty List if no logging events are available
     */
    @ServiceDescription(description = "Requests log events from a gateway")
    List<LogEvent> getLogEvents(Date startDate, Date endDate);

    /**
     * Triggers the local machine to send a copy of its system_logs.idb to the requesting remote machine.
     * @param requestingServer the server making the request for the log file
     * @return "SUCCESS", or "FAIL" with an error message appended.
     */
    @ServiceDescription(description = "Requests a system_logs.idb file from a gateway")
    String requestLogFile(ServerId requestingServer, int downloadId);

    /**
     * Triggers the local machine to send a copy of its system_logs.idb to a remote machine.
     * @param filePath The full path of the file to send. This is marked with the @FileStream annotation so that the
     *                 gateway network knows that the path points to a file that needs to be streamed.
     * @return A message reporting the size of the received file in bytes
     */
    @ServiceDescription(description = "Pushes a system_logs.idb file from the local gateway to a remote gateway")
    @FileStream(filePathField = 0)
    String pushLogFile(String filePath);
}
