package com.inductiveautomation.ignition.examples.task;

import java.util.List;

/**
 * Created by mattgross on 9/21/2016. A convenient wrapper to hide some of the complexity of the task settings
 * persistent record.
 */
public interface RemoteGatewaySelection {
    List<String> getSelectedGateways();

    void setSelectedGateways(List<String> selectedGateways);
}
