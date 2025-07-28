package com.inductiveautomation.ignition.examples.eventstream.handler.gateway;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.inductiveautomation.eventstream.EventPayload;
import com.inductiveautomation.eventstream.HandlerDescriptor;
import com.inductiveautomation.eventstream.gateway.api.EventStreamContext;
import com.inductiveautomation.eventstream.gateway.api.EventStreamHandler;
import com.inductiveautomation.eventstream.gateway.api.expression.EventStreamExpressionFactory;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.examples.eventstream.handler.ExampleHandlerConfig;
import com.inductiveautomation.ignition.examples.eventstream.handler.ExampleHandlerModule;

public class ExampleHandler implements EventStreamHandler {

    public static Factory createFactory() {
        return new Factory() {
            @Override
            public HandlerDescriptor getDescriptor() {
                return new HandlerDescriptor(
                    ExampleHandlerModule.MODULE_ID,
                    ExampleHandlerModule.MODULE_NAME,
                    "Writes payload to a specified path"
                );
            }

            @Override
            public EventStreamHandler create(EventStreamContext context, JsonObject jsonConfig) throws Exception {
                return new ExampleHandler(context, ExampleHandlerConfig.fromJson(jsonConfig));
            }
        };
    }

    private final EventStreamContext context;
    private final ExampleHandlerConfig config;

    private FileWriter writer;
    private FileWriter testWriter;

    public ExampleHandler(EventStreamContext context, ExampleHandlerConfig config) {
        this.context = context;
        this.config = config;
    }

    @Override
    public void onStartup(EventStreamExpressionFactory expressionFactory) throws Exception {
        context.logger().debugf("Starting %s", ExampleHandlerModule.MODULE_NAME);
        writer = new FileWriter(config.filePath());
        if (config.useTestFilePath()) {
            testWriter = new FileWriter(config.testFilePath());
        }
    }

    @Override
    public void onShutdown() {
        context.logger().debugf("Shutting down %s", ExampleHandlerModule.MODULE_NAME);
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

    /**
     * Handles a list of EventPayloads, either when the event stream runs or using the Dry Run option for testing.
     */
    @Override
    public void handle(List<EventPayload> list, boolean testMode) throws Exception {
        context.logger().debugf("Handling events using testMode? %b", testMode);
        for (EventPayload event : list) {
            writeEvent(event, testMode);
        }
    }

    private void writeEvent(EventPayload event, boolean testMode) throws IOException {
        String dataAsString = event.getData().toString();
        context.logger().debugf("Writing data: %s", dataAsString);
        if (testMode) {
            if (config.useTestFilePath()) {
                testWriter.write(dataAsString);
                testWriter.write("\n");
                testWriter.flush();
            } else {
                context.logger().debugf("In Test Mode. Writing to log: %s", dataAsString);
            }
        } else {
            writer.write(dataAsString);
            writer.write("\n");
            writer.flush();
        }
    }
}