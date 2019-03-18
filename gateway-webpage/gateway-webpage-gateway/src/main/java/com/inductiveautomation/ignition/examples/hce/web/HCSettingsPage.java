package com.inductiveautomation.ignition.examples.hce.web;

import com.inductiveautomation.ignition.examples.hce.records.HCSettingsRecord;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.IgnitionWebApp;
import com.inductiveautomation.ignition.gateway.web.components.RecordEditForm;
import com.inductiveautomation.ignition.gateway.web.models.LenientResourceModel;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;
import org.apache.wicket.Application;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Filename: HCSettingsPage
 * Author: Perry Arellano-Jones
 * Created on: 5/21/15
 * Project: home-connect-example
 *
 * HCSettings extends  {@link RecordEditForm} to provide a page where we can edit records in our PersistentRecord.
 */
public class HCSettingsPage extends RecordEditForm {
    public static final String[] PATH = {"homeconnect", "settings"};

    public HCSettingsPage(final IConfigPage configPage) {
        super(configPage, null, new LenientResourceModel("HomeConnect.nav.settings.panelTitle"),
            ((IgnitionWebApp) Application.get()).getContext().getPersistenceInterface().find(HCSettingsRecord.META, 0L)
        );
    }


    @Override
    public Pair<String, String> getMenuLocation() {
        return Pair.of("homeconnect", "settings");
    }

}
