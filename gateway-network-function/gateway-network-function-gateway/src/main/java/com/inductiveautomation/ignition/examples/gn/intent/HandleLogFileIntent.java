package com.inductiveautomation.ignition.examples.gn.intent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Maps;
import com.inductiveautomation.metro.api.DiagnosticIdentifier;
import com.inductiveautomation.metro.api.Intent;
import com.inductiveautomation.metro.api.IntentInfo;
import com.inductiveautomation.metro.api.ServerId;
import com.inductiveautomation.metro.impl.codecs.SimpleStreamAwareObject;
import org.apache.log4j.Logger;

@IntentInfo(system = "GatewayNetworkExample", task = "handleLogFile",
    description = "Handles a log file streamed over the Gateway Network")
public class HandleLogFileIntent implements Intent<SimpleStreamAwareObject, Void> {
    public static final String NAME = "handle_log_file";
    private final Map<Integer, CompletableFuture<String>> futuresMap = Maps.newConcurrentMap();
    private final Logger logger = Logger.getLogger("HandleLogFileIntent");

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Void receive(ServerId sourceServerAddr, SimpleStreamAwareObject sso) throws Exception {
        String localFilePath = sso.getFilePath();
        String localFileName = sso.getFileName();
        int downloadId = sso.getDownloadId();

        // Grab the future from the map and return the local file path of the streamed file
        CompletableFuture<String> future = futuresMap.remove(downloadId);

        if (future == null) {
            logger.error(String.format("No future found for download for file '%s' downloaded to '%s' from server '%s'",
                localFileName,
                localFilePath,
                sourceServerAddr.toDescriptiveString()));
        } else {
            future.complete(localFilePath);
        }

        return null;
    }

    public void addPendingFuture(Integer downloadId, CompletableFuture<String> future) {
        futuresMap.put(downloadId, future);
    }

    public void removePendingFuture(Integer downloadId) {
        futuresMap.remove(downloadId);
    }
}
