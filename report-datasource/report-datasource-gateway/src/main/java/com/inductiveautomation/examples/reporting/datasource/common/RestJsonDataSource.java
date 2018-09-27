package com.inductiveautomation.examples.reporting.datasource.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterators;
import com.inductiveautomation.examples.reporting.datasource.gateway.GatewayHook;
import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import com.inductiveautomation.reporting.common.resource.DataSourceConfig;
import com.inductiveautomation.examples.reporting.datasource.utils.JsonUtils;
import com.inductiveautomation.reporting.gateway.api.GatewayDataSourceRegistry;
import com.inductiveautomation.reporting.gateway.api.ReportDataSource;
import com.inductiveautomation.reporting.gateway.api.ReportExecutionContext;
import com.inductiveautomation.reporting.gateway.api.ReportExecutionContext.ReportLoggerEx;
import com.inductiveautomation.rm.base.RMKey;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides the Gateway scoped functionality for the example Datasource.  By implementing {@link ReportDataSource}
 * and registering it in the {@link GatewayHook#startup(LicenseState)}
 * through {@link GatewayDataSourceRegistry}.
 * @author Perry Arellano-Jones
 */
public class RestJsonDataSource implements ReportDataSource {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /*
     * ReportLoggerEx is a wrapper around LoggerEx that adds WARN and ERROR level log messages to the data collection
     * error popup that is displayed in the Report Design/Preview panels as well as the Report Viewer component in
     * Vision windows.
     */
    ReportLoggerEx log;

    /**
     * This method is where data can be added to the data map.  We utilitize the
     * {@link ReportExecutionContext} to get the data map, and simple inject our data as an additional
     * entry.
     * @param reportExecutionContext provides
     * @param _configObject is the configuration object we defined in Common scope.  It's information originates from
     *                      the Designer's {@link DataSourceConfig} panel.
     * @param extraConfigs Extra info the datasource may need. The key is the datasource ID.
     */
    @Override
    public void gatherData(ReportExecutionContext reportExecutionContext, Serializable _configObject,
                           Map<String, Object> extraConfigs) {
        log = reportExecutionContext.getLog();

        /* Collect information from the config object and add to the data map with the proper ID. */
        Map<String, Object> data = reportExecutionContext.getExecutionData().getData();

        RestJsonDataObject configObject = (RestJsonDataObject) _configObject;

        // verify the user entered a valid datakey
        if (configObject != null && RMKey.isKey(configObject.getKey())) {
            String key = configObject.getKey();
            String url;

            // get the url
            if (!TypeUtilities.isNullOrEmpty(configObject.getUrl())) {
                url = configObject.getUrl();
            } else {
                log.warnf("Url field of REST JSON Datasource %s requires a URL to attempt data collection.", key);
                return;
            }

            log.tracef("Attempting to collect data from '%s' for %s Datakey.", configObject.getUrl(), configObject.getKey());

            try {
                //collect the data as a String
                String jsonString = collectData(url);
                if (!TypeUtilities.isNullOrEmpty(jsonString)) {
                    try {
                        log.tracef("Attempting to build JSON Object from data string.");
                        // map our simple string to a reporting-engine friendly map
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Map<String,Object> mappedJSON = JsonUtils.toMap(jsonObject);
                        if (mappedJSON != null) {
                            data.put(configObject.getKey(), mappedJSON);
                        }
                    } catch (JSONException jse) {
                        // may have an array of json objects, so we try to build a data set out of them
                        log.tracef("Failed to create single JSON object from data, attempting JSONArray from data.", jse);
                        try {
                            JSONArray jsonArray = new JSONArray(jsonString);
                            Dataset dataset = createDatasetFromJSONArray(jsonArray);
                            if (dataset != null) {
                                data.put(configObject.getKey(), dataset);
                            }
                            log.tracef("Dataset created from Json Array and added to map.");
                        } catch (Exception e) {
                            log.warnf("Could not parse JSONArray from \'%s\'.", configObject.getUrl(), e);
                        }
                    }
                } else {
                    log.warnf("No Json data could be found at \'%s\'", configObject.getUrl());
                }
            } catch (Exception e){
                log.warnf("Could not create usable data from \'%s\'", configObject.getUrl(), e);
            }
        }
    }


    /**
     * Looks through the {@link JSONArray}, evaluating each {@link JSONObject} to determine which has the largest
     * set of keys.  This process is useful because not all JSON objects in an array may have the same key set.  There
     * are some assumptions made that the largest keyset will contain all the keys available in those of lower sets.
     * This is far from true, but this assumption is allowed for the sake of simplicity for this example.
     * @param jsonArray a {@link JSONArray}
     * @return a JSONObject containing the most keys found in the array.  May be empty.
     */
    private JSONObject findReferenceObjectIndex(JSONArray jsonArray) throws JSONException {
        JSONObject reference = null;
        if (jsonArray != null && jsonArray.length() > 0) {
            reference = jsonArray.getJSONObject(0);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                if (Iterators.size(jo.keys()) > Iterators.size(reference.keys())) {
                    reference = jo;
                }
            }
        }
        return reference == null ? new JSONObject() : reference;
    }

    /**
     * Creates and returns {@link Dataset} with String column types from a {@link JSONArray}.  Returns empty if no data
     * was found and an exception was not thrown.
     */
    private Dataset createDatasetFromJSONArray(JSONArray jsonArray) throws JSONException {
        Dataset ds = null;

        if (jsonArray.length() >= 1) {
            // JSON objects in an array may not have values for each key, so make sure we
            // get the full keyset
            JSONObject reference = findReferenceObjectIndex(jsonArray);

            // get column names from the reference object
            List<String> colNames = new ArrayList<>();
            Iterator keys = reference.keys();
            while (keys.hasNext()) {
                colNames.add(keys.next().toString());
            }

            // now start building dataset to pass into report engine
            DatasetBuilder builder = new DatasetBuilder();
            builder.colNames(colNames.toArray(new String[0]));

            // set the column types. We're using all strings in this example, but we could be
            // checking for numeric types and setting different column types to enable calculations
            Class[] types = new Class[colNames.size()];
            for (int i = 0; i < types.length; i++) {
                types[i] = String.class;
            }
            builder.colTypes(types);

            // now create rows for each json list value
            for (int row = 0; row < jsonArray.length(); row++) {
                String[] rowData = new String[colNames.size()];
                for (int col = 0; col < colNames.size(); col++) {
                    try {
                        rowData[col] = jsonArray.getJSONObject(row).get(colNames.get(col)).toString();
                    } catch (JSONException e) {
                        // ignore because it just means this object didn't have our key
                    }
                }
                builder.addRow(rowData);
            }
            ds = builder.build();
        }
        return ds != null ? ds : new BasicDataset();
    }


    public String getId() {
        return RestJsonDataObject.ID;
    }

    /**
     * Attempts to connect to a Url using {@link OkHttpClient} and return the body of the reply as a String, if it is
     * JSON mime type.
     * @param url String specifying the
     * @return the body of the http response received from the specified Url
     */
    private String collectData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        // get headers to check the content type out of
        Headers headers = null;
        if (response != null) {
            headers = response.headers();
        }

        // at least try to make sure we have JSON data.
        boolean isJSON = false;
        if (headers != null) {
            isJSON = MediaType.parse(headers.get("Content-Type")).equals(JSON);
        }

        return isJSON ? response.body().string() : "";
    }
}
