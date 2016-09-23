package com.inductiveautomation.ignition.examples.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.inductiveautomation.ignition.common.util.RAFCircularBuffer;
import com.inductiveautomation.ignition.common.util.RAFCircularBuffer.Filter;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.api.ServiceManager;
import com.inductiveautomation.metro.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by mattgross on 9/19/2016. Implementation of the GetLogsService, and performs the actual work of sending
 * log entries and files.
 */
public class GetLogsServiceImpl implements GetLogsService {

    private GatewayContext context;
    private Logger logger = LogManager.getLogger("GetLogsService");

    public GetLogsServiceImpl(GatewayContext context){
        this.context = context;
    }

    @Override
    public List<LoggingEvent> getLogEvents(Date startDate, Date endDate) {

        List<LoggingEvent> events = new ArrayList<>();
        File logFile = new File(context.getLogsDir() + File.separator + "logs.bin");
        if(logFile.exists()){
            try{
                // Open a backwards-iterating 20MB buffer
                RAFCircularBuffer<LoggingEvent> buffer = new RAFCircularBuffer<>(logFile, 1024 * 20, false);
                events = buffer.iterateBackward(new DateFilter(startDate, endDate));
            }
            catch(IOException e){
                logger.error("IOException thrown when reading log buffer", e);
            }

        }
        else{
            logger.warn(String.format("%s does not exist. No logs will be returned", logFile.getAbsolutePath()));
        }

        return events;
    }

    @Override
    public String requestWrapperLog(ServerId requestingServer) {

        // First, make a temporary copy of wrapper.log
        File wrapperFile = new File(context.getLogsDir() + File.separator + "wrapper.log");
        File tempLog = null;
        if(wrapperFile.exists()){
            tempLog = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + ".wrapper.log");
            try {
                FileUtils.copyFile(wrapperFile, tempLog);
            } catch (IOException e) {
                String msg = GetLogsService.FAIL_MSG + ": IOException thrown when copying wrapper.log to temp file: " + e.getMessage();
                logger.error(msg);
                return msg;
            }
        }
        else{
            logger.warn(String.format("%s does not exist. Log file will not be sent", wrapperFile.getAbsolutePath()));
            return GetLogsService.FAIL_MSG + ": wrapper.log file was not found";
        }

        String finalResult = null;
        try{
            // Call the streamWrapperLog() function on the other side and stream the local wrapper.log.
            ServerId localId = context.getGatewayAreaNetworkManager().getServerAddress();
            ServiceManager sm = context.getGatewayAreaNetworkManager().getServiceManager();
            finalResult = sm.getService(requestingServer, GetLogsService.class).get().streamWrapperLog(tempLog.getAbsolutePath(), localId);
            return finalResult;
        }
        catch(Exception e){
            finalResult = String.format(GetLogsService.FAIL_MSG
                    + ": Error streaming backup to remote. Check error log on remote machine '%s'. Exception=%s",
                    requestingServer.toDescriptiveString(),
                    e.getMessage());

            logger.error(finalResult);
            return finalResult;
        }
    }

    @Override
    public String streamWrapperLog(String wrapperFilePath, ServerId sourceServerId) {
        // The passed wrapperFilePath holds the path to the streamed wrapper.log file on the local system.
        // From here, you can move the file to an archived location as you see fit. You will need to return
        // a success or fail message so the original caller can report on the task execution.
        logger.info(String.format("Streamed wrapper.log file for server '%s' is available here: %s",
                sourceServerId.toDescriptiveString(),
                wrapperFilePath));

        return SUCCESS_MSG;
    }

    private static class DateFilter implements Filter<LoggingEvent> {

        private Long startTime;
        private Long endTime;
        private boolean isFinished = false;

        /**
         * Pass startDate as null to not have any filter for start date. The same applies to endDate. If both dates
         * are null, all log events are returned.
         * @param startDate
         * @param endDate
         */
        public DateFilter(Date startDate, Date endDate){
            if(startDate != null){
                this.startTime = startDate.getTime();
            }

            if(endDate != null){
                this.endTime = endDate.getTime();
            }
        }

        @Override
        public boolean accept(LoggingEvent event) {
            Long timestamp = event.getTimeStamp();

            if(startTime != null && timestamp < startTime){
                // We are going backwards, so we know that any entries after this are also out of range.
                isFinished = true;
                return false;
            }

            if(endTime != null && timestamp > endTime){
                return false;
            }

            return true;
        }

        @Override
        public boolean finished() {
            return isFinished;
        }
    }
}
