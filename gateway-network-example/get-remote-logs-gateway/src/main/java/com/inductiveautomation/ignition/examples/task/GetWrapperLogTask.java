package com.inductiveautomation.ignition.examples.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.inductiveautomation.ignition.examples.service.GetLogsService;
import com.inductiveautomation.ignition.gateway.gan.GatewayAreaNetworkManager;
import com.inductiveautomation.ignition.gateway.tasks.AbstractTask;
import com.inductiveautomation.ignition.gateway.tasks.TaskContext;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.api.ServerState;
import com.inductiveautomation.metro.api.services.ServiceState;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by mattgross on 9/15/2016. When the task system fires a scheduled task, it creates a task instance here.
 * This task instance does the actual task work, and reports results to the task system.
 */
public class GetWrapperLogTask extends AbstractTask {

    private static final String UPDATE_MSG = "Retrieval task complete for %d of %d server(s)";
    private static final String LOGGER_NAME = "GetWrapperLogTask";
    private GetWrapperLogTaskSettingsRecord taskSettings;
    private List<Throwable> errors = new ArrayList<>();
    private Logger logger = LogManager.getLogger(LOGGER_NAME);

    public GetWrapperLogTask(UUID id, GetWrapperLogTaskSettingsRecord settings) {
        super(id);
        this.taskSettings = settings;
    }

    @Override
    protected String getLoggerName() {
        return LOGGER_NAME;
    }

    /**
     * Executes the work of the task. To keep things simple, this task processes the servers in sequential order. There
     * is functionality within the API to process items in a task in parallel, at the expense of a much more complex task
     * implementation.
     *
     * @param context
     **/
    @Override
    protected void doTaskWork(TaskContext context) throws Exception {

        List<String> selectedGateways = taskSettings.getSelectedGateways();
        int completedGateways = 0;
        GatewayAreaNetworkManager gm = context.getGatewayContext().getGatewayAreaNetworkManager();

        // Retrieve this machine's server id, so that the remote system can use the address to stream the file
        // to this system.
        ServerId localAddr = gm.getServerAddress();

        for(String gateway: selectedGateways){
            logger.info(String.format("Will run the wrapper log retrieval task for '%s'", gateway));
            ServerId remoteId = ServerId.fromString(gateway);

            ServiceState serviceState = gm.getServiceManager().getRemoteServiceState(remoteId, GetLogsService.class);

            // Verify the service actually exists on the remote machine before trying to use it.
            if(!(ServiceState.Available == serviceState)){
                String errMsg = String.format(
                        "GetLogsService is not available on remote server '%s', returned service status=%s",
                        gateway,
                        serviceState.toString());
                logger.error(errMsg);
                Throwable t = new Throwable(errMsg);
                errors.add(t);

                completedGateways++;
                continue;
            }
            else{
                // When the remote service runs, it will stream a copy of its wrapper.log file to this machine in a
                // temp location. GetLogsServiceImpl.streamWrapperLog() handles the streamed wrapper.log.
                // Note that this service call will time out in 60 seconds if a result has not been received.
                String status = gm.getServiceManager().getService(remoteId, GetLogsService.class).get().requestWrapperLog(localAddr);
                if(GetLogsService.SUCCESS_MSG.equals(status)){
                    logger.info(String.format("%s wrapper.log was been successfully retrieved", gateway));
                }
                else{
                    // The remote machine was unable to stream the file to this machine.
                    String errMsg = String.format("%s wrapper.log retrieval failed, check the remote Gateway's logs", gateway);
                    Throwable t = new Throwable(errMsg);
                    errors.add(t);
                    logger.error(errMsg);
                }

            }

            completedGateways++;
            logger.info(String.format(UPDATE_MSG, completedGateways, selectedGateways.size()));
        }

    }

    /**
     * Returns a List of all the exceptions that occurred when running a task. If no exceptions occurred, an empty List
     * is returned.
     *
     * @return
     */
    @Override
    public List<Throwable> getErrors() {
        return errors;
    }
}
