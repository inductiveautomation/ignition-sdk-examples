package com.inductiveautomation.ignition.examples.eventstream.handler.gateway;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.inductiveautomation.eventstream.EventPayload;
import com.inductiveautomation.eventstream.gateway.api.EventStreamHandler;
import com.inductiveautomation.eventstream.gateway.api.expression.EventStreamExpressionFactory;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.eventstream.handler.ExampleHandlerConfig;

public class ExampleHandler implements EventStreamHandler {
    private final LoggerEx logger = LoggerEx.newBuilder().build(ExampleHandler.class);

    private final ExampleHandlerConfig config;

    private FileWriter writer;
    private FileWriter testWriter;

    public ExampleHandler(ExampleHandlerConfig config) {
        this.config = config;
    }

    @Override
    public void onStartup(EventStreamExpressionFactory expressionFactory) throws Exception {
        writer = new FileWriter(config.filePath());
        if (config.useTestFilePath()) {
            testWriter = new FileWriter(config.testFilePath());
        }
    }

    @Override
    public void onShutdown() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (testWriter != null) {
            try {
                testWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void handle(List<EventPayload> list, boolean testMode) throws Exception {
        for (EventPayload event : list) {
            writeEvent(event, testMode);
        }
    }

    private void writeEvent(EventPayload event, boolean testMode) throws IOException {
        var dataAsString = event.getData().toString();

        if (testMode) {
            if (config.useTestFilePath()) {
                testWriter.write(dataAsString);
                testWriter.write("\n");
                testWriter.flush();
            } else {
                logger.infof("In Test Mode. Writing to log: %s", dataAsString);
            }
        } else {
            writer.write(dataAsString);
            writer.write("\n");
            writer.flush();
        }
    }

    public static EventStreamHandler.Factory createFactory() {
        return (context, config) -> new ExampleHandler(
            ExampleHandlerConfig.fromJson(config)
        );
    }
}