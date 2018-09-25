package com.inductiveautomation.ignition.examples.service;

import java.util.Date;
import java.util.List;

import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.gateway.gan.security.TrialPeriodProtected;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.impl.services.annotations.FileStream;

/**
 * Created by mattgross on 9/19/2016.
 */
@TrialPeriodProtected(moduleId = "com.example.get-remote-logs")
public interface GetLogsService {

    String WRAPPER_ALLOWED_PROP = "wrapper-retrieve-allowed";
    String ACCESS_KEY = "wrapper-service-key";
    String SUCCESS_MSG = "SUCCESS";
    String FAIL_MSG = "FAIL";

    /**
     * Provides a List of LoggingEvents to the calling machine. An empty List is returned if no logging events are
     * available.
     * @param startDate returns logging events with a timestamp after this date. Set to null to not use a start date.
     * @param endDate returns logging events with a timestamp before this date. Set to null to not use an end date.
     * @return a List of LoggingEvents, or an empty List if no logging events are available
     */
    List<LogEvent> getLogEvents(Date startDate, Date endDate);

    /**
     * Triggers the local machine to send a copy of its wrapper log to the requesting remote machine.
     * @param requestingServer the server making the request for the wrapper log
     * @param accessKey a String sent from the requesting server and which is validated by this server's service
     *                  security. If the sent key and the configured key do not match, a security exception is thrown.
     * @return "SUCCESS", or "FAIL" with an error message appended.
     */
    String requestWrapperLog(ServerId requestingServer, String accessKey);

    /**
     * Handles the wrapper.log that has been sent from the remote machine.
     * @param wrapperFilePath on the send side, this field is used to set the location of the wrapper.log on disk. On
     *                        the receive side, this field holds the location of the wrapper.log copy that has just been
     *                        received, as the file must be saved in a temporary location after download. Use of the
     *                        FileStream annotation is what causes this behavior, and is what enables
     *                        files to be sent between machines over the Gateway Network. Note that (filePathField = 0)
     *                        indicates that the first method parameter is being used for this purpose.
     * @param sourceServerId the server which has sent the wrapper.log
     * @return "SUCCESS", or "FAIL" with an error message appended.
     */
    @FileStream(filePathField = 0)
    String streamWrapperLog(String wrapperFilePath, ServerId sourceServerId);
}
