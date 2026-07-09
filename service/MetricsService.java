package com.example.gateway.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

    public void increment(String metric) {
        counters.computeIfAbsent(metric, k -> new AtomicLong(0)).incrementAndGet();
    }

    public Map<String, Long> snapshot() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        counters.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }
}

