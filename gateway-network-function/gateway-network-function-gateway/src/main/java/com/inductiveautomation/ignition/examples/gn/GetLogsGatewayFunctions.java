package com.inductiveautomation.ignition.examples.gn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.inductiveautomation.eam.gateway.intents.ReceiveDownloadIntent;
import com.inductiveautomation.eam.gateway.module.EAMGatewayHook;
import com.inductiveautomation.ignition.common.logging.LogEvent;
import com.inductiveautomation.ignition.common.script.hints.NoHint;
import com.inductiveautomation.ignition.examples.gn.service.GetLogsService;
import com.inductiveautomation.ignition.gateway.gan.GatewayNetworkManager;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.api.ServiceManager;
import com.inductiveautomation.metro.api.ex.ServiceException;
import com.inductiveautomation.metro.api.services.ServiceState;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.python.core.Py;

/**
 * This class does the actual work of calling remote gateways and getting their log entries or files. A note about
 * this class: it implements the `GetLogsFunctions` interface. Why wouldn't we add this interface to the parent
 * AbstractGetLogsFunctions class? The reason is that the GetLogsFunctions interface has an @RpcInterface annotation
 * on it. The gateway RPC system needs this annotation to be able to register the GetLogsFunctions interface. Without
 * it, the gateway will throw this error when trying to make RPC calls from the designer/client:
 * "java.lang.IllegalArgumentException: No RPC interfaces found on implementation class"
 */
public class GetLogsGatewayFunctions extends AbstractGetLogsFunctions implements GetLogsFunctions {
    private final GatewayContext context;
    private final Logger logger = Logger.getLogger("GetLogsFunctions");
    private final GatewayHook gatewayHook;

    public GetLogsGatewayFunctions(GatewayContext context, GatewayHook gatewayHook) {
        this.context = context;
        this.gatewayHook = gatewayHook;
    }


    @Override
    public Map<String, List<LogEvent>> getRemoteLogEntries(List<String> remoteServers, Date startDate, Date endDate) {
        return getLogEntriesInternal(remoteServers, startDate, endDate);
    }

    @Override
    public byte[] getRemoteLogFile(String remoteServer) throws IOException {
        return getLogFileInternal(remoteServer);
    }

    @NoHint
    @Override
    public Map<String, List<LogEvent>> getLogEntriesInternal(List<String> remoteServers, Date startDate,
                                                             Date endDate) {
        HashMap<String, List<LogEvent>> logsMap = new HashMap<>();

        ServiceManager sm = context.getGatewayNetworkManager().getServiceManager();
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
                List<LogEvent> events =
                    sm.getService(serverId, GetLogsService.class).get().getLogEvents(startDate, endDate);
                logsMap.put(server, events);
            }
        }

        return logsMap;
    }

    @Nullable
    @Override
    public byte[] getLogFileInternal(String remoteServer) throws IOException {
        GatewayNetworkManager gm = context.getGatewayNetworkManager();
        ServiceManager sm = gm.getServiceManager();

        ServerId serverId = ServerId.fromString(remoteServer);
        String idbFilePath = null;

        // First, verify that the service is available on the remote machine before trying to call.
        ServiceState state = sm.getRemoteServiceState(serverId, GetLogsService.class);
        if(state != ServiceState.Available) {
            logger.error(String.format("Service was unavailable for server '%s', current state is %s",
                serverId.toDescriptiveString(),
                state.toString()));
            return null;
        }
        else {
            /* Getting a file as a service call result is a bit tricky with the gateway network. The file must be
            streamed between gateways, and service call results can't be streamed directly. To get around this,
            we have to use the built-in ReceiveDownloadIntent and a CompletableFuture. We register a new
            CompletableFuture with the ReceiveDownloadIntent using a unique download id. Then we pass that id to the
            service call. The other gateway will retrieve the file and then make a ReceiveDownloadIntent gateway
            network call. It passes the file stream and download id as part of the call. On this gateway, the
            ReceiveDownloadIntent fires and locates our waiting CompletableFuture using the download id. It completes
            our CompletableFuture and passes over the full path of the downloaded log file (the file is stored in a
            temporary location, but can be moved as needed).

            NB. The gateways must have EAM configured, and be setup in a Controller/Agent configuration. If EAM
            hasn't been configured on the instance yet, the ReceiveDownloadIntent will not be available.
             */
            ReceiveDownloadIntent downloadIntent = (ReceiveDownloadIntent)
                gm.retrieveIntent(ReceiveDownloadIntent.NAME)
                    .orElseThrow(() -> new IllegalStateException(
                        "ReceiveDownloadIntent not registered; EAM has not been configured."
                    ));

            String localGwbkPath = null;
            CompletableFuture<String> downloadFuture = new CompletableFuture<>();

            int nextId = EAMGatewayHook.getInstance().getNextIntentId();
            downloadIntent.addPendingFuture(nextId, downloadFuture);

            String response = null;
            try {
                response =
                    sm.getService(serverId, GetLogsService.class).get().requestLogFile(gm.getServerAddress(), nextId);

                if (response.startsWith(GetLogsService.FAIL_MSG)) {
                    throw new ServiceException(response);
                }

                // Wait for our CompletableFuture inside the ReceiveDownloadIntent to fire. We will give it 10 minutes max.
                logger.info("Waiting for agent to send over system_logs.idb");
                idbFilePath = downloadFuture.get(10, TimeUnit.MINUTES);
            } catch (Exception e) {
                // Clean up the CompletableFuture since it will never be completed normally.
                downloadFuture.cancel(true);
                downloadIntent.removePendingFuture(nextId);
                throw new IOException(e.getMessage());
            }
        }

        Path localTempFolderPath = Path.of(idbFilePath);
        if (!Files.exists(localTempFolderPath)) {
            throw Py.IOError("Downloaded system_logs.idb file not found at '%s'".formatted(localTempFolderPath));
        }

        logger.info("system_logs.idb received from other gateway");
        return Files.readAllBytes(localTempFolderPath);
    }

    @NoHint
    @Override
    public String pushLogFileInternal(String remoteGateway) throws IOException {
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

        ServerId serverId = ServerId.fromString(remoteGateway);
        ServiceManager sm = context.getGatewayNetworkManager().getServiceManager();

        // First, verify that the service is available on the remote machine before trying to call.
        ServiceState state = sm.getRemoteServiceState(serverId, GetLogsService.class);
        if(state != ServiceState.Available){
            String msg = String.format("Service was unavailable for server '%s', current state is %s",
                serverId.toDescriptiveString(),
                state.toString());
            logger.error(msg);
            return msg;
        }
        else {
            // The service call will time out after 60 seconds if no service ACK is received from the remote Gateway.
            logger.info("Pushing log file '%s' to remote gateway '%s'".formatted(
                tempLog.getAbsolutePath(),
                serverId.toDescriptiveString()));

            return sm.getService(serverId, GetLogsService.class).get().pushLogFile(tempLog.getAbsolutePath());
        }
    }
}
