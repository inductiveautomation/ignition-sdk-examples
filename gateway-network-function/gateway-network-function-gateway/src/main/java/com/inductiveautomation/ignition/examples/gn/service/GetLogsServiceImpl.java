package com.inductiveautomation.ignition.examples.gn.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import com.inductiveautomation.eam.gateway.intents.ReceiveDownloadIntent;
import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.logging.LogQueryConfig;
import com.inductiveautomation.ignition.common.logging.LogQueryConfig.LogQueryConfigBuilder;
import com.inductiveautomation.ignition.common.logging.LogResults;
import com.inductiveautomation.ignition.examples.gn.GatewayHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.metro.api.Message;
import com.inductiveautomation.metro.api.MessageQueue;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.api.ServerInterface;
import com.inductiveautomation.metro.impl.codecs.SimpleStreamAwareObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of the GetLogsService, and performs the actual work of sending log entries and files.
 */

public class GetLogsServiceImpl implements GetLogsService {

    private final GatewayContext context;
    private final Logger logger = LoggerFactory.getLogger("GetLogsService");
    private final GatewayHook gatewayHook;

    public GetLogsServiceImpl(GatewayContext context, GatewayHook gatewayHook) {
        this.context = context;
        this.gatewayHook = gatewayHook;
    }

    @Override
    public List<LogEvent> getLogEvents(Date startDate, Date endDate) {
        LogQueryConfigBuilder query = LogQueryConfig.newBuilder();
        if(startDate != null){
            query.newerThan(startDate.getTime());
        }

        if(endDate != null){
            query.olderThan(endDate.getTime());
        }

        LogQueryConfig filter = query.build();
        LogResults result = context.getLoggingManager().queryLogEvents(filter);

        return result.getEvents();
    }

    @Override
    public String requestLogFile(ServerId requestingServer, int downloadId) {
        // Make a temporary copy of the logs db
        File tempLog;
        try {
            tempLog = gatewayHook.getTempLogFile();
        } catch (IOException e) {
            String msg = GetLogsService.FAIL_MSG + ": IOException thrown when copying system_logs.idb to temp file: "
                + e.getMessage();
            logger.error(msg);
            return msg;
        }

        // Create an implementation of FileStreamAwareObject and place the log's file path inside
        String path = tempLog.getAbsolutePath();
        SimpleStreamAwareObject sso = new SimpleStreamAwareObject(path, downloadId, tempLog.getName());

        // Pass the object to the server for delivery
        ServerInterface server = context.getGatewayNetworkManager().getServerIfKnown(requestingServer);

        if (server == null) {
            String errMsg = String.format("Server with id '%s' cannot be located",
                requestingServer.toDescriptiveString());
            logger.error(errMsg);
            return FAIL_MSG + ":" + errMsg;
        } else {
            // Sending message back using the diagnostic queue, which has a 5-minute timeout by default
            MessageQueue queue = server.getQueue(ServerInterface.DIAGNOSTIC_INFO_QUEUE_ID).orElse(server);

            // This is already a response, so we use a post operation, which itself does not check for a response.
            queue.post(Message.buildWithDiagnosticMsg(ReceiveDownloadIntent.NAME, ReceiveDownloadIntent.class, sso));
            return SUCCESS_MSG;
        }
    }

    @Override
    public String pushLogFile(String filePath) {
        // This is called on the receiving gateway
        Path path = Paths.get(filePath);
        logger.info("Received log file '%s'. Size is %d bytes.".formatted(path, path.toFile().length()));
        return "Remote gateway received log file. Received size was %d bytes.".formatted(path.toFile().length());
    }

}
