package com.inductiveautomation.ignition.examples.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.logging.LogQueryConfig;
import com.inductiveautomation.ignition.common.logging.LogQueryConfig.LogQueryConfigBuilder;
import com.inductiveautomation.ignition.common.logging.LogResults;
import com.inductiveautomation.ignition.gateway.gan.security.SecuredEntity;
import com.inductiveautomation.ignition.gateway.gan.security.SecuredEntityImplementation;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.api.ServiceManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by mattgross on 9/19/2016. Implementation of the GetLogsService, and performs the actual work of sending
 * log entries and files.
 * Adding 'implements SecuredEntityImplementation' allows use to use some convenience methods available in that
 * interface, such as the getClassSecurityConfig() method.
 */
@SecuredEntity(id="get-wrapper-log",
        nameKey="remotelogging.services.security.name",
        configPropDescriptionFactory=GLSecurityDescriptorFactory.class,
        configFactory=GLSecurityConfigFactory.class)
public class GetLogsServiceImpl implements GetLogsService, SecuredEntityImplementation<GLSecurityConfigValues, Void> {

    private GatewayContext context;
    private Logger logger = LoggerFactory.getLogger("GetLogsService");

    public GetLogsServiceImpl(GatewayContext context){
        this.context = context;
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
    public String requestWrapperLog(ServerId requestingServer, String accessKey) {
        try{
            // Verify that the access key sent from remote Gateway matches the one configured in the service security settings.
            getClassSecurityConfig().checkAccessKey(accessKey);

            // Then verify that the 'Allow wrapper log access' setting is enabled.
            getClassSecurityConfig().checkAllowWrapperSetting();

            // Then verify that all dynamic properties are enabled.
            getClassSecurityConfig().checkDynamicProperties();
        }
        catch(SecurityException e){
            logger.warn(e.getMessage());
            throw e;
        }


        // If all security checks passed, we can continue. Start by making a temporary copy of wrapper.log
        File wrapperFile = new File(context.getSystemManager().getLogsDir() + File.separator + "wrapper.log");
        File tempLog;
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

        String finalResult;
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


}
