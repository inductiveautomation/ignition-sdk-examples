package com.inductiveautomation.ignition.examples.hce;

import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;

/**
 * Filename: HCConnectionSettings
 * Author: Perry Arellano-Jones
 * Created on: 6/8/15
 * Project: home-connect-example
 */
public class HCConnectionSettings extends PersistentRecord {

    public static final RecordMeta<HCConnectionSettings> META = new RecordMeta<>(HCConnectionSettings.class, "hc_connectionsettings");

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }
}
