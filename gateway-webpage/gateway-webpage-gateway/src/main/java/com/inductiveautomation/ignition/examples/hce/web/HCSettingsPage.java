package com.inductiveautomation.ignition.examples.hce.web;

import com.inductiveautomation.ignition.examples.hce.GatewayHook;
import com.inductiveautomation.ignition.examples.hce.records.HCSettingsRecord;
import com.inductiveautomation.ignition.gateway.model.IgnitionWebApp;
import com.inductiveautomation.ignition.gateway.web.components.RecordEditForm;
import com.inductiveautomation.ignition.gateway.web.models.LenientResourceModel;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Application;

/**
 * Filename: HCSettingsPage
 * Author: Perry Arellano-Jones
 * Created on: 5/21/15
 * Project: home-connect-example
 *
 * HCSettings extends  {@link RecordEditForm} to provide a page where we can edit records in our PersistentRecord.
 */
public class HCSettingsPage extends RecordEditForm {
    public static final Pair<String, String> MENU_LOCATION =
        Pair.of(GatewayHook.CONFIG_CATEGORY.getName(), "homeconnect");

    public HCSettingsPage(final IConfigPage configPage) {
        super(configPage, null, new LenientResourceModel("HomeConnect.nav.settings.panelTitle"),
            ((IgnitionWebApp) Application.get()).getContext().getPersistenceInterface().find(HCSettingsRecord.META, 0L)
        );
    }


    @Override
    public Pair<String, String> getMenuLocation() {
        return MENU_LOCATION;
    }

}
