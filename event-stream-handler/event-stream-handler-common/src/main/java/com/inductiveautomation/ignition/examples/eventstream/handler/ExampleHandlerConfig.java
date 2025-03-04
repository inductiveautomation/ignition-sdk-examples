package com.inductiveautomation.ignition.examples.eventstream.handler;

import com.inductiveautomation.ignition.common.gson.JsonObject;

/**
 * @param filePath The path where the Event Stream payload is written to
 */
public record ExampleHandlerConfig(String filePath, String testFilePath, boolean useTestFilePath) {

    private static final String FILE_PATH = "filePath";
    private static final String TEST_FILE_PATH = "testFilePath";
    private static final String USE_TEST_FILE_PATH = "useTestFilePath";

    public JsonObject toJson() {
        var json = new JsonObject();
        json.addProperty(FILE_PATH, filePath);
        json.addProperty(TEST_FILE_PATH, testFilePath);
        json.addProperty(USE_TEST_FILE_PATH, useTestFilePath);
        return json;
    }

    public static ExampleHandlerConfig fromJson(JsonObject config) {
        if (config == null || config.isEmpty()) {
            return defaultConfig();
        }

        return new ExampleHandlerConfig(
            config.get(FILE_PATH).getAsString(),
            config.get(TEST_FILE_PATH).getAsString(),
            config.get(USE_TEST_FILE_PATH).getAsBoolean()
        );
    }

    public static ExampleHandlerConfig defaultConfig() {
        return new ExampleHandlerConfig("", "", false);
    }
}
