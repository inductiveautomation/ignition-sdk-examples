package com.inductiveautomation.ignition.examples.eventstream.source;

import com.inductiveautomation.ignition.common.gson.JsonObject;

public record ExampleSourceConfig(String textToStream) {

    public static final String TEXT_TO_STREAM = "textToStream";

    public JsonObject toJson() {
        var json = new JsonObject();
        json.addProperty(TEXT_TO_STREAM, textToStream);
        return json;
    }

    public static ExampleSourceConfig fromJson(JsonObject config) {
        if (config == null || config.isEmpty()) {
            return defaultConfig();
        }
        return new ExampleSourceConfig(config.get(TEXT_TO_STREAM).getAsString());
    }

    public static ExampleSourceConfig defaultConfig() {
        return new ExampleSourceConfig("");
    }
}
