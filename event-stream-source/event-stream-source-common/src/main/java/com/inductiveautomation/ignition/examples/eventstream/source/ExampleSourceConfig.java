package com.inductiveautomation.ignition.examples.eventstream.source;

import com.inductiveautomation.ignition.common.gson.JsonObject;

public record ExampleSourceConfig(String textToStream) {

    public JsonObject toJson() {
        var json = new JsonObject();
        json.addProperty("textToStream", textToStream);
        return json;
    }

    public static ExampleSourceConfig fromJson(JsonObject json) {
        return new ExampleSourceConfig(json.get("textToStream").getAsString());
    }
}
