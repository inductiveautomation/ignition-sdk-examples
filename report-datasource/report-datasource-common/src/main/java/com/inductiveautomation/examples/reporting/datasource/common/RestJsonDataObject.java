package com.inductiveautomation.examples.reporting.datasource.common;

import java.io.Serializable;

/**
 * Provides a small serializable object that contains data and key in the form of a String.  This data object
 * is sent via RPC call between the Report Workspace and the gateway during report design/preview.
 * @author Perry Arellano-Jones
 */
public class RestJsonDataObject implements Serializable {
    public static final String ID = "com.ia.reporting.examples.rest.json.datasource";
    private static final long serialVersionUID = 1;

    private String key;
    private String url;

    // serialization friendly constructor
    public RestJsonDataObject() {
    }

    public RestJsonDataObject(String key, String url) {
        this.url = url;
        this.key = key;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        if (obj == null || getClass() != obj.getClass()){
            return false;
        }

        RestJsonDataObject that = (RestJsonDataObject) obj;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }
}
