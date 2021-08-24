package com.inductiveautomation.ignition.examples.hce.web;

import java.util.Collections;
import java.util.List;

import com.inductiveautomation.ignition.examples.hce.records.HCSettingsRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistenceSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.pages.status.overviewmeta.InfoLine;
import com.inductiveautomation.ignition.gateway.web.pages.status.overviewmeta.OverviewContributor;
import com.inductiveautomation.ignition.gateway.web.pages.status.overviewmeta.SystemsEntry;
import simpleorm.dataset.SQuery;

/**
 * This Overview Contributor doesn't do anything useful, but shows how to add one if your module can make use of it.
 */
public class HomeConnectOverviewContributor implements OverviewContributor {

    @Override
    public Iterable<SystemsEntry> getSystemsEntries(GatewayContext context) {

        int connectionCount = 0;
        PersistenceSession session = context.getPersistenceInterface().getSession();
        try {
            SQuery<HCSettingsRecord> query = new SQuery<>(HCSettingsRecord.META);
            List<HCSettingsRecord> connectionList = session.query(query);
            if (connectionList != null){
                connectionCount = connectionList.size();
            }
        } finally {
            session.close();
        }

        SystemsEntry connections = new SystemsEntry(
                "Home Connections",
                new InfoLine(String.format("%d configured", connectionCount), false),
                "/web/status/sys.homeconnect"
        );
        return Collections.singletonList(connections);
    }
}
