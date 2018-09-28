package com.inductiveautomation.ignition.examples.hce.records;

import com.inductiveautomation.ignition.gateway.localdb.persistence.BooleanField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.Category;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IdentityField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IntField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.StringField;
import simpleorm.dataset.SFieldFlags;

/**
 * Filename: HCSettingsRecord
 * Author: Perry Arellano-Jones
 * Created on: 6/8/15
 * Project: home-connect-example
 */
public class HCSettingsRecord extends PersistentRecord {

    public static final RecordMeta<HCSettingsRecord> META = new RecordMeta<HCSettingsRecord>(
            HCSettingsRecord.class, "HCSettingsRecord").setNounKey("HCSettingsRecord.Noun").setNounPluralKey(
            "HCSettingsRecord.Noun.Plural");

    public static final IdentityField Id = new IdentityField(META);

    //Hub Settings -- for our imaginary IoT controller device
    public static final StringField HCHubName = new StringField(META, "HomeConnectHubName", SFieldFlags.SMANDATORY);
    public static final BooleanField BroadcastSSID = new BooleanField(META, "BroadcastSSID").setDefault(false);
    public static final IntField HCDeviceCount = new IntField(META, "DeviceCount", SFieldFlags.SMANDATORY);
    public static final StringField HCIPAddress = new StringField(META, "IPAddress", SFieldFlags.SMANDATORY);
    public static final BooleanField AllowInterop = new BooleanField(META, "AllowInterop").setDefault(true);
    public static final IntField HCPowerOutput = new IntField(META, "PowerOutput", SFieldFlags.SMANDATORY).setDefault(23);

    // create categories for our record entries, getting titles from the HCSettingsRecord.properties, and
    // ordering through integer ranking
    static final Category HubConfiguration = new Category("HCSettingsRecord.Category.Configuration", 1000).include(HCHubName, HCIPAddress);
    static final Category Security = new Category("HCSettingsRecord.Category.Security", 1001).include(BroadcastSSID, HCDeviceCount, AllowInterop);
    static final Category Power = new Category("HCSettingsRecord.Category.Power", 1001).include(HCPowerOutput);



    // accessors for our record entries
    public void setAllowInterop(Boolean allow) {
        setBoolean(AllowInterop, allow);
    }
    public boolean getAllowInterop() {
        return getBoolean(AllowInterop);
    }

    public void setId(Long id) {
        setLong(Id, id);
    }

    public Long getId() {
        return getLong(Id);
    }

    public void setBroadcastSSID(Boolean broadcast){
        setBoolean(BroadcastSSID, broadcast);
    }

    public boolean getBroadcastSSID() {
        return getBoolean(BroadcastSSID);
    }

    public void setHCDeviceCount(Integer count) {
        setInt(HCDeviceCount, count);
    }
    public Integer getHCDeviceCount() {
        return getInt(HCDeviceCount);
    }

    public void setHCHubName(String name) {
        setString(HCHubName, name);
    }

    public String getHCHubName() {
        return getString(HCHubName);
    }

    public void setHCIPAddress(String ip){
        setString(HCIPAddress, ip);
    }

    public String getHCIPAddress() {
        return getString(HCIPAddress);
    }

    public Integer getHCPowerOutput() {
        return getInt(HCPowerOutput);
    }

    public void setHCPowerOutput(Integer power){
        setInt(HCPowerOutput, power);
    }

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }
}
