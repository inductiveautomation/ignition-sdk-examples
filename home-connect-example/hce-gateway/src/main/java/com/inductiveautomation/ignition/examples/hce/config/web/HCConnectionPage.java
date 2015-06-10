package com.inductiveautomation.ignition.examples.hce.config.web;

import java.util.List;

import com.inductiveautomation.ignition.examples.hce.HCConnectionSettings;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.web.components.RecordActionTable;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;

/**
 * Filename: HCConnectionPage
 * Author: Perry Arellano-Jones
 * Created on: 6/8/15
 * Project: home-connect-example
 *
 * Provides an example of a page where we might view a table of values by extending {@link RecordActionTable}
 * to see our configured connections (one per row).  By default, the parent class will provide us with some convenient
 * links to edit or delete an entry, as well as an <i>add</i> link below.
 */
public class HCConnectionPage extends RecordActionTable<HCConnectionSettings> {
    public static final String[] PATH = {"homeconnect", "connections"};
    transient List<ICalculatedField<HCConnectionSettings>> calcFields;

    public HCConnectionPage( IConfigPage configPage) {
        super(configPage);
    }


    @Override
    public String[] getMenuPath() {
        return PATH;
    }

    @Override
    protected RecordMeta<HCConnectionSettings> getRecordMeta() {
        return HCConnectionSettings.META;
    }
}
