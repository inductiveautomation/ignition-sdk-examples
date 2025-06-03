package com.inductiveautomation.ignition.examples.eventstream.source.gateway;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.inductiveautomation.eventstream.EventPayload;
import com.inductiveautomation.eventstream.SourceDescriptor;
import com.inductiveautomation.eventstream.gateway.api.EventStreamContext;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceConfig;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceModule;

import static com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceModule.MODULE_ID;

/**
 * Given a list of comma separated items, this source will emit each item in the list every second.
 */
public class ExampleSource implements EventStreamSource {

    public static Factory createFactory() {
        return new Factory() {
            @Override
            public SourceDescriptor getDescriptor() {
                return new SourceDescriptor(
                    ExampleSourceModule.MODULE_ID,
                    ExampleSourceModule.MODULE_NAME,
                    "Given a comma delimited string, will stream each value on a 15 second interval."
                );
            }

            @Override
            public EventStreamSource create(EventStreamContext context, JsonObject jsonConfig) {
                return new ExampleSource(context, ExampleSourceConfig.fromJson(jsonConfig));
            }
        };
    }


    private final EventStreamContext context;
    private final AtomicReference<Subscriber> subscriber = new AtomicReference<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    private final String[] items;
    private Timer timer;

    public ExampleSource(EventStreamContext context, ExampleSourceConfig config) {
        this.context = context;
        items = config.textToStream().split(",");
    }

    @Override
    public void onStartup(Subscriber subscriber) {
        this.subscriber.set(subscriber);
        context.logger().infof("Starting %s", MODULE_ID);
        start();
    }

    @Override
    public void onShutdown() {
        context.logger().infof("Shutting down %s", MODULE_ID);
        subscriber.set(null);
        stop();
    }

    private void start() {
        stop();
        context.logger().infof("Starting timer to emit items every second");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var currentIndex = counter.get();
                var nextIndex = currentIndex >= items.length - 1 ? 0 : currentIndex + 1;
                if (counter.compareAndSet(currentIndex, nextIndex)) {
                    context.logger().infof("Emitting item: %s", items[nextIndex]);
                    subscriber.get().submitEvent(EventPayload.builder(
                        items[nextIndex]).build()
                    );
                }
            }
        }, 0, 1_000);
    }

    private void stop() {
        if (timer == null) {
            return;
        }

        timer.cancel();
        timer.purge();
    }
}