package com.inductiveautomation.ignition.examples.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.inductiveautomation.ignition.gateway.localdb.persistence.LongField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.StringField;
import com.inductiveautomation.ignition.gateway.tasks.GatewayTaskRecord;
import org.apache.commons.lang3.StringUtils;
import simpleorm.dataset.SFieldFlags;

/**
 * Created by mattgross on 9/15/2016.
 */
public class GetWrapperLogTaskSettingsRecord extends PersistentRecord implements RemoteGatewaySelection {

    public static final RecordMeta<GetWrapperLogTaskSettingsRecord> META = new RecordMeta<>(GetWrapperLogTaskSettingsRecord.class,
            "getlogstasksettings");

    /**
     * Needed to link this record with a GatewayTaskRecord
     */
    public static final LongField ProfileId = new LongField(META, "profileid", SFieldFlags.SMANDATORY,
            SFieldFlags.SPRIMARY_KEY);

    public static final ReferenceField<GatewayTaskRecord> Profile = new ReferenceField<>(META,
            GatewayTaskRecord.META, "profile", ProfileId);

    public static final StringField TargetGateways = new StringField(META, "targetGateways");

    public static final StringField AccessKey = new StringField(META, "accessKey").setDefault("");

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }

    @Override
    public List<String> getSelectedGateways() {
        String targets = getString(TargetGateways);
        List<String> servers = new ArrayList<>();

        if(!StringUtils.isEmpty(targets)){
            String[] targetsArr = targets.split(",");
            servers.addAll(Arrays.asList(targetsArr));
        }

        return servers;
    }

    @Override
    public void setSelectedGateways(List<String> gateways) {

        String selected = "";
        if(gateways.size() > 0){
            selected = StringUtils.join(gateways, ",");
        }

        setString(TargetGateways, selected);
    }

    @Override
    public void setAccessKey(String accessKey) {
        setString(AccessKey, accessKey);
    }

    @Override
    public String getAccessKey(){
        String accesskey = getString(AccessKey);

        if(accesskey == null){
            accesskey = "";
        }
        return accesskey;
    }
}
