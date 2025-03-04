package com.inductiveautomation.ignition.examples.eventstream.source.gateway;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.inductiveautomation.eventstream.EventPayload;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceConfig;

import static com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceModule.MODULE_ID;

/**
 * Given a list of comma separated items, this source will emit each item in the list every second.
 */
public class ExampleSource implements EventStreamSource {

    private final LoggerEx logger = LoggerEx.newBuilder().build(ExampleSource.class);

    private final AtomicReference<Subscriber> subscriber = new AtomicReference<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    private final String[] items;
    private Timer timer;

    public ExampleSource(ExampleSourceConfig config) {
        items = config.textToStream().split(",");
    }

    @Override
    public void onStartup(Subscriber subscriber) {
        this.subscriber.set(subscriber);
        logger.infof("Starting %s", MODULE_ID);
        start();
    }

    @Override
    public void onShutdown() {
        logger.infof("Shutting down %s", MODULE_ID);
        subscriber.set(null);
        stop();
    }

    private void start() {
        stop();
        logger.infof("Starting timer to emit items every second");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var currentIndex = counter.get();
                var nextIndex = currentIndex >= items.length - 1 ? 0 : currentIndex + 1;
                if (counter.compareAndSet(currentIndex, nextIndex)) {
                    logger.infof("Emitting item: %s", items[nextIndex]);
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

    public static EventStreamSource.Factory createFactory() {
        return (context, config) -> new ExampleSource(
            ExampleSourceConfig.fromJson(config)
        );
    }
}