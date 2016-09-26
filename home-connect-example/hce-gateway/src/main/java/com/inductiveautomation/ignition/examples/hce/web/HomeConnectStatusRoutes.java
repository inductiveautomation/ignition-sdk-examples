package com.inductiveautomation.ignition.examples.hce.web;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.hce.records.HCSettingsRecord;
import com.inductiveautomation.ignition.gateway.dataroutes.RequestContext;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.dataroutes.WicketAccessControl;
import com.inductiveautomation.ignition.gateway.localdb.persistence.BooleanField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.IntField;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistenceSession;
import com.inductiveautomation.ignition.gateway.localdb.persistence.StringField;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import simpleorm.dataset.SFieldFlags;
import simpleorm.dataset.SQuery;

import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup.TYPE_JSON;

/**
 * Filename: HomeConnectStatusRoutes
 * Created on 9/22/16
 * Author: Kathy Applebaum
 * Copyright: Inductive Automation 2016
 * Project: home-connect-example
 * <p/>
 */
public class HomeConnectStatusRoutes {

    private final LoggerEx log = LogUtil.getLogger(getClass().getSimpleName());
    private final RouteGroup routes;

    public HomeConnectStatusRoutes(GatewayContext context, RouteGroup group) {
        this.routes = group;
    }

    public void mountRoutes() {
        routes.newRoute("/status/connections")
                .handler(this::getConnectionStatus)
                .type(TYPE_JSON)
                .restrict(WicketAccessControl.STATUS_SECTION)
                .mount();

        // Not used in this example. Shown here as an example of using parameters passed in from javascript
        routes.newRoute("/status/connections/:name")
                .handler((req, res) -> getConnectionDetail(req, res, req.getParameter("name")))
                .type(TYPE_JSON)
                .restrict(WicketAccessControl.STATUS_SECTION)
                .mount();
    }

    public JSONObject getConnectionStatus(RequestContext requestContext, HttpServletResponse httpServletResponse) throws JSONException {
        GatewayContext context = requestContext.getGatewayContext();
        JSONObject json = new JSONObject();
        PersistenceSession session = context.getPersistenceInterface().getSession();
        try {
            SQuery<HCSettingsRecord> query = new SQuery<>(HCSettingsRecord.META);
            List<HCSettingsRecord> connectionList = session.query(query);
            if (connectionList != null){
                json.put("count", connectionList.size());
                JSONArray jsonArray = new JSONArray();
                json.put("connections", jsonArray);
                for (HCSettingsRecord record : connectionList){
                    if (record != null) {
                        JSONObject connectionJson = new JSONObject();
                        jsonArray.put(connectionJson);
                        connectionJson.put("HomeConnectHubName", record.getHCHubName());
                        connectionJson.put("BroadcastSSID", record.getBroadcastSSID());
                        connectionJson.put("DeviceCount", record.getHCDeviceCount());
                        connectionJson.put("IPAddress", record.getHCIPAddress());
                        connectionJson.put("AllowInterop", record.getAllowInterop());
                        connectionJson.put("PowerOutput", record.getHCPowerOutput());
                    }
                }
            }
        } finally {
            session.close();
        }
        return json;
    }

    // Not used in this example. Shown here as an example of using parameters passed in from javascript.
    // Any time the parameter is something that could have anything outside of a-zA-Z0-9, be sure to encode/decode the
    // parameter.
    public JSONObject getConnectionDetail(RequestContext requestContext, HttpServletResponse httpServletResponse, String connectionName) throws JSONException, UnsupportedEncodingException {
        GatewayContext context = requestContext.getGatewayContext();
        JSONObject json = new JSONObject();
        String decodedConnectionName = URLDecoder.decode(connectionName, "UTF-8");
        // todo add functionality
        json.put("connection", "Hello world from "+decodedConnectionName);
        return json;
    }
}

