package com.example.gateway.controller;



import com.example.gateway.Model.Event;
import com.example.gateway.service.GatewayService;
import com.example.gateway.service.MetricsService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GatewayController {

    private final GatewayService gatewayService;
    private final MetricsService metricsService;

    public GatewayController(GatewayService gatewayService, MetricsService metricsService) {
        this.gatewayService = gatewayService;
        this.metricsService = metricsService;
    }

    /** Submit a new transaction event */
    @PostMapping("/events")
    public ResponseEntity<Event> submitEvent(@RequestBody Event event) {
        validate(event);
        metricsService.increment("requests.post_events");
        return ResponseEntity.status(HttpStatus.CREATED).body(gatewayService.submitEvent(event));
    }

    /** Get single event by ID — works even if Account Service is down */
    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable String id) {
        metricsService.increment("requests.get_event");
        return ResponseEntity.ok(gatewayService.getEvent(id));
    }

    /** List events for an account in chronological order — works even if Account Service is down */
    @GetMapping("/events")
    public ResponseEntity<List<Event>> listEvents(@RequestParam String account) {
        metricsService.increment("requests.list_events");
        return ResponseEntity.ok(gatewayService.getEventsForAccount(account));
    }

    /** Health check */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "event-gateway"));
    }

    /** Custom metrics endpoint */
    @GetMapping("/metrics/custom")
    public ResponseEntity<Map<String, Long>> metrics() {
        return ResponseEntity.ok(metricsService.snapshot());
    }


    private void validate(Event e) {
        if (blank(e.getEventId()))        throw new IllegalArgumentException("eventId is required");
        if (blank(e.getAccountId()))      throw new IllegalArgumentException("accountId is required");
        if (e.getType() == null)          throw new IllegalArgumentException("type must be CREDIT or DEBIT");
        if (e.getAmount() <= 0)           throw new IllegalArgumentException("amount must be > 0");
        if (blank(e.getCurrency()))       throw new IllegalArgumentException("currency is required");
        if (e.getEventTimestamp() == null) throw new IllegalArgumentException("eventTimestamp is required");
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }
}
