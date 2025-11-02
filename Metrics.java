package org.smartcity.metrics;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
    private final Map<String, Long> counters = new HashMap<>();
    private long tStart = 0;
    private long elapsed = 0;

    public void start() {
        tStart = System.nanoTime();
    }

    public void stop() {
        if (tStart != 0) {
            elapsed = System.nanoTime() - tStart;
        }
    }

    public long getElapsed() {
        return elapsed;
    }

    public void inc(String key) {
        counters.put(key, counters.getOrDefault(key, 0L) + 1);
    }

    public void incBy(String key, long v) {
        counters.put(key, counters.getOrDefault(key, 0L) + v);
    }

    public long get(String key) {
        return counters.getOrDefault(key, 0L);
    }

    public Map<String, Long> snapshot() {
        return new HashMap<>(counters);
    }
}
