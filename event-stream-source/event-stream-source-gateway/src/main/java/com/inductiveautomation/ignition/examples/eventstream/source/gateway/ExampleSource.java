package com.inductiveautomation.ignition.examples.eventstream.source.gateway;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.inductiveautomation.eventstream.EventPayload;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceConfig;

/**
 * Given a list of comma separated items, this source will emit each item in the list every second.
 */
public class ExampleSource implements EventStreamSource {

    private final AtomicReference<Subscriber> subscriber = new AtomicReference<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    private final String[] items;
    private final Timer timer = new Timer();


    public ExampleSource(ExampleSourceConfig config) {
        items = config.textToStream().split(",");
    }

    @Override
    public void onStartup(Subscriber subscriber) {
        this.subscriber.set(subscriber);
        start();
    }

    @Override
    public void onShutdown() {
        subscriber.set(null);
    }

    public void start() {
        stop();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int index = currentIndex.getAndIncrement();
                if (index >= items.length) {
                    currentIndex.set(0);
                }
                subscriber.get().submitEvent(EventPayload.builder(
                    items[index]).build()
                );
            }
        }, 0, 1_000);
    }

    private void stop() {
        timer.cancel();
    }

    public static EventStreamSource.Factory createFactory() {
        return (context, config) -> new ExampleSource(
            ExampleSourceConfig.fromJson(config)
        );
    }
}