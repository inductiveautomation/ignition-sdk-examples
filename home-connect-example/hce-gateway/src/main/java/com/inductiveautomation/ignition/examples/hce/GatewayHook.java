package com.inductiveautomation.ignition.examples.hce;

import com.inductiveautomation.ignition.examples.hce.config.web.HCConnectionPage;
import com.inductiveautomation.ignition.examples.hce.config.web.HCSettingsPage;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.components.LabelConfigMenuNode;
import com.inductiveautomation.ignition.gateway.web.components.LinkConfigMenuNode;

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


    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;

        // Register GatewayHook.properties by registering the GatewayHook.class with BundleUtils
        BundleUtil.get().addBundle(GatewayHook.class);

        initMenu();
    }

    private void initMenu() {
        /* header is the top-level title in the gateway config page, e.g. System, Configuration, etc */
        LabelConfigMenuNode header = new LabelConfigMenuNode(HCON_MENU_PATH[0], "GatewayHook.nav.header");
        header.setPosition(801);

        context.getConfigMenuModel().addConfigMenuNode(null, header);

        /* Create the nodes/links that will exist under our parent nav header */
        LinkConfigMenuNode settingsNode = new LinkConfigMenuNode("settings",
                "GatewayHook.nav.settings.title",
                HCSettingsPage.class);
        LinkConfigMenuNode devicesNode = new LinkConfigMenuNode("connections",
                "GatewayHook.nav.connections.title",
                HCConnectionPage.class);


        /* register our nodes with the context config menu model */
        context.getConfigMenuModel().addConfigMenuNode(HCON_MENU_PATH, settingsNode);
        context.getConfigMenuModel().addConfigMenuNode(HCON_MENU_PATH, devicesNode);
    }


    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {

    }

}
