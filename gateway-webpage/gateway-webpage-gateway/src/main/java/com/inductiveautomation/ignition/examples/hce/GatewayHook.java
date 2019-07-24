package com.inductiveautomation.ignition.examples.hce;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.hce.records.HCSettingsRecord;
import com.inductiveautomation.ignition.examples.hce.web.HCSettingsPage;
import com.inductiveautomation.ignition.examples.hce.web.HomeConnectOverviewContributor;
import com.inductiveautomation.ignition.examples.hce.web.HomeConnectStatusRoutes;
import com.inductiveautomation.ignition.gateway.ContextState;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IRecordListener;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.components.AbstractNamedTab;
import com.inductiveautomation.ignition.gateway.web.components.ConfigPanel;
import com.inductiveautomation.ignition.gateway.web.models.ConfigCategory;
import com.inductiveautomation.ignition.gateway.web.models.DefaultConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.INamedTab;
import com.inductiveautomation.ignition.gateway.web.models.KeyValue;
import com.inductiveautomation.ignition.gateway.web.pages.BasicReactPanel;
import com.inductiveautomation.ignition.gateway.web.pages.status.StatusCategories;
import com.inductiveautomation.ignition.gateway.web.pages.status.overviewmeta.OverviewContributor;
import org.apache.wicket.Application;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Filename: GatewayHook.java
 * Author: Perry Arellano-Jones
 * Created on: 5/21/15
 * Project: home-connect-example
 *
 * The "gateway hook" is the entry point for a module on the gateway.
 *
 * This example uses the new status and config pages for 7.9 and later.
 *
 */
public class GatewayHook extends AbstractGatewayModuleHook {
    private GatewayContext context;

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());

    /**
     * This sets up the status panel which we'll add to the statusPanels list. The controller will be
     * HomeConnectStatusRoutes.java, and the model and view will be in our javascript folder. The status panel is optional
     * Only add if your module will provide meaningful info.
     */
    private static final INamedTab HCE_STATUS_PAGE = new AbstractNamedTab(
            "homeconnect",
            StatusCategories.SYSTEMS,
            "HomeConnect.nav.status.header") {

        @Override
        public WebMarkupContainer getPanel(String panelId) {
            // We've set  GatewayHook.getMountPathAlias() to return hce, so we need to use that alias here.
            return new BasicReactPanel(panelId, "/main/res/hce/js/homeconnectstatus.js", "homeconnectstatus");
        }

        @Override
        public Iterable<String> getSearchTerms(){
            return Arrays.asList("home connect", "hce");
        }
    };

    /**
     * This sets up the config panel
     */
    public static final ConfigCategory CONFIG_CATEGORY =
        new ConfigCategory("HomeConnect", "HomeConnect.nav.header", 700);

    @Override
    public List<ConfigCategory> getConfigCategories() {
        return Collections.singletonList(CONFIG_CATEGORY);
    }

    /**
     * An IConfigTab contains all the info necessary to create a link to your config page on the gateway nav menu.
     * In order to make sure the breadcrumb and navigation works properly, the 'name' field should line up
     * with the right-hand value returned from {@link ConfigPanel#getMenuLocation}. In this case name("homeconnect")
     * lines up with HCSettingsPage#getMenuLocation().getRight()
     */
    public static final IConfigTab HCE_CONFIG_ENTRY = DefaultConfigTab.builder()
            .category(CONFIG_CATEGORY)
            .name("homeconnect")
            .i18n("HomeConnect.nav.settings.title")
            .page(HCSettingsPage.class)
            .terms("home connect settings")
            .build();

    @Override
    public List<? extends IConfigTab> getConfigPanels() {
        return Collections.singletonList(
            HCE_CONFIG_ENTRY
        );
    }

    /**
     * We'll add an overview contributor. This is optional -- only add if your module will provide meaningful info.
     */
    private final OverviewContributor overviewContributor = new HomeConnectOverviewContributor();

    @Override
    public Optional<OverviewContributor> getStatusOverviewContributor() {
        return Optional.of(overviewContributor);
    }


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


    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {
        /* remove our bundle */
        BundleUtil.get().removeBundle("HomeConnect");
    }

    /**
     * The following methods are used by the status panel. Only add these if you are providing a status panel.
     */


    // getMountPathAlias() allows us to use a shorter mount path. Use caution, because we don't want a conflict with
    // other modules by other authors.
    @Override
    public Optional<String> getMountPathAlias() {
        return Optional.of("hce");
    }

    // Use this whenever you have mounted resources
    @Override
    public Optional<String> getMountedResourceFolder() {
        return Optional.of("mounted");
    }

    // Define your route handlers here
    @Override
    public void mountRouteHandlers(RouteGroup routes) {
        new HomeConnectStatusRoutes(context, routes).mountRoutes();
    }

    @Override
    public List<? extends INamedTab> getStatusPanels() {
        return Collections.singletonList(HCE_STATUS_PAGE);
    }
}
