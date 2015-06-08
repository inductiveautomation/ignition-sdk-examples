package com.inductiveautomation.ignition.examples.hce.config.web;

import com.inductiveautomation.ignition.examples.hce.HCConnectionSettings;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.components.RecordEditForm;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;
import org.apache.wicket.Application;
import org.apache.wicket.model.Model;

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
        super(configPage, null, Model.of("HomeConnect Settings"),
                ((GatewayContext) Application.get()).getPersistenceInterface().find(HCConnectionSettings.META, 0L));
    }


    @Override
    public String[] getMenuPath() {
        return PATH;
    }

}
