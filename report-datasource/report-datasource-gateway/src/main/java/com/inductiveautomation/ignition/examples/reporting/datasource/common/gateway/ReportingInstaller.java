package com.inductiveautomation.ignition.examples.reporting.datasource.common.gateway;

import com.inductiveautomation.ignition.examples.reporting.datasource.common.RestJsonDataSource;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.reporting.gateway.api.GatewayDataSourceRegistry;

public class ReportingInstaller {

    public static void setup(GatewayContext context) {
        var registry = GatewayDataSourceRegistry.get(context);
        registry.register(new RestJsonDataSource());
    }
}