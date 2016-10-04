package com.inductiveautomation.ignition.examples.task;

import java.util.List;

/**
 * Created by mattgross on 9/21/2016. A convenient wrapper to hide some of the complexity of the task settings
 * persistent record.
 */
public interface RemoteGatewaySelection {
    /**
     * Returns a list of selected Gateways after creating a get-wrapper-logs task.
     * @return
     */
    List<String> getSelectedGateways();

    void setSelectedGateways(List<String> selectedGateways);

    /**
     * The access key is sent to the remote Gateway when requesting its wrapper log. The remote Gateway
     * will reject the request if its configured access key does not match. The local access key is set when creating a
     * get-wrapper-logs task, and the remote access key is configured in the remote Gateway under Service Security.
     * @return
     */
    String getAccessKey();

    void setAccessKey(String accessKey);
}
