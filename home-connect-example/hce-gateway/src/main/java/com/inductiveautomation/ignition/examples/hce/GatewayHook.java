package com.inductiveautomation.ignition.examples.hce;

import java.sql.SQLException;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.examples.hce.web.HCSettingsPage;
import com.inductiveautomation.ignition.examples.hce.records.HCSettingsRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IRecordListener;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.components.LabelConfigMenuNode;
import com.inductiveautomation.ignition.gateway.web.components.LinkConfigMenuNode;
import com.inductiveautomation.ignition.gateway.web.models.KeyValue;
import org.apache.log4j.Logger;

/**
 * Filename: GatewayHook.java
 * Author: Perry Arellano-Jones
 * Created on: 5/21/15
 * Project: home-connect-example
 *
 * The "gateway hook" is the entry point for a module on the gateway.
 *
 * This example uses {@link BundleUtil} to register our i18n property bundles to the internationalization system, and
 * adds {@link LabelConfigMenuNode} and {@link LinkConfigMenuNode} to create Wicket navigation items for display in
 * the Gateway Configuration panel.
 *
 */
public class GatewayHook extends AbstractGatewayModuleHook {
    private static final String[] HCON_MENU_PATH = {"homeconnect"};
    private GatewayContext context;

    private final Logger log = Logger.getLogger(getClass());


    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;

        log.debug("Beginning setup of HomeConnect Module");

        // Register GatewayHook.properties by registering the GatewayHook.class with BundleUtils
        BundleUtil.get().addBundle("HomeConnect", getClass(), "HomeConnect");

        //Verify tables for persistent records if necessary
        verifySchema(context);

        // create records if needed
        maybeCreateHCSettings(context);

        // get the settings record and do something with it...
        HCSettingsRecord theOneRecord = context.getLocalPersistenceInterface().find(HCSettingsRecord.META, 0L);
        log.info("Hub name: " + theOneRecord.getHCHubName());
        log.info("IP address: " + theOneRecord.getHCIPAddress());

        // listen for updates to the settings record...
        HCSettingsRecord.META.addRecordListener(new IRecordListener<HCSettingsRecord>() {
            @Override
            public void recordUpdated(HCSettingsRecord hcSettingsRecord) {
                log.info("recordUpdated()");
            }

            @Override
            public void recordAdded(HCSettingsRecord hcSettingsRecord) {
                log.info("recordAdded()");
            }

            @Override
            public void recordDeleted(KeyValue keyValue) {
                log.info("recordDeleted()");
            }
        });

        //initialize our Gateway nav menu
        initMenu();

        log.debug("Setup Complete.");
    }

    private void verifySchema(GatewayContext context) {
        try {
            context.getSchemaUpdater().updatePersistentRecords(HCSettingsRecord.META);
        } catch (SQLException e) {
            log.error("Error verifying persistent record schemas for HomeConnect records.", e);
        }
    }

    public void maybeCreateHCSettings(GatewayContext context) {
        log.trace("Attempting to create HomeConnect Settings Record");
        try {
            HCSettingsRecord settingsRecord = context.getLocalPersistenceInterface().createNew(HCSettingsRecord.META);
            settingsRecord.setId(0L);
            settingsRecord.setHCIPAddress("192.168.1.99");
            settingsRecord.setHCHubName("HomeConnect Hub");
            settingsRecord.setHCPowerOutput(23);
            settingsRecord.setHCDeviceCount(15);
            settingsRecord.setBroadcastSSID(false);

        /*
			 * This doesn't override existing settings, only replaces it with these if we didn't
			 * exist already.
			 */
            context.getSchemaUpdater().ensureRecordExists(settingsRecord);
        } catch (Exception e) {
            log.error("Failed to establish HCSettings Record exists", e);
        }

        log.trace("HomeConnect Settings Record Established");
    }

    private void initMenu() {
        /* header is the top-level title in the gateway config page, e.g. System, Configuration, etc */
        LabelConfigMenuNode header = new LabelConfigMenuNode(HCON_MENU_PATH[0], "HomeConnect.nav.header");
        header.setPosition(801);

        context.getConfigMenuModel().addConfigMenuNode(null, header);

        /* Create the nodes/links that will exist under our parent nav header */
        LinkConfigMenuNode settingsNode = new LinkConfigMenuNode("settings",
                "HomeConnect.nav.settings.title",
                HCSettingsPage.class);

        /* register our nodes with the context config menu model */
        context.getConfigMenuModel().addConfigMenuNode(HCON_MENU_PATH, settingsNode);
    }


    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {
        /* remove our bundle */
        BundleUtil.get().removeBundle("HomeConnect");

        /* remove our nodes from the menu */
        context.getConfigMenuModel().removeConfigMenuNode(HCON_MENU_PATH);
    }

}
