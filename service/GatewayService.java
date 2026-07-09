package com.example.gateway.service;

import com.example.gateway.Model.Event;
import com.example.gateway.client.AccountClient;
import com.example.gateway.exception.DuplicateEventException;
import com.example.gateway.exception.EventNotFoundException;
import com.exmaple.gateway.repo.EventRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GatewayService {

    private static final Logger log = LoggerFactory.getLogger(GatewayService.class);

    private final EventRepository eventRepository;
    private final AccountClient accountClient;
    private final MetricsService metricsService;

    public GatewayService(EventRepository eventRepository,
                          AccountClient accountClient,
                          MetricsService metricsService) {
        this.eventRepository = eventRepository;
        this.accountClient = accountClient;
        this.metricsService = metricsService;
    }

    public Event submitEvent(Event event) {
        String traceId = UUID.randomUUID().toString();

        // Structured log context
        MDC.put("traceId", traceId);
        MDC.put("eventId", event.getEventId());
        MDC.put("accountId", event.getAccountId());

        try {
            // ── IDEMPOTENCY CHECK ──────────────────────────────────────────
            eventRepository.findById(event.getEventId()).ifPresent(existing -> {
                log.info("Duplicate event rejected eventId={}", event.getEventId());
                metricsService.increment("events.duplicate");
                throw new DuplicateEventException(existing);
            });

            // ── FORWARD TO ACCOUNT SERVICE (circuit breaker applied) ───────
            ResponseEntity<String> response = accountClient.applyTransaction(event, traceId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Account Service returned non-2xx status={}", response.getStatusCode());
                metricsService.increment("events.account_service_error");
                throw new RuntimeException("Unexpected status from Account Service: "
                        + response.getStatusCode());
            }

            Event saved = eventRepository.save(event);
            log.info("Event accepted eventId={} type={} amount={} currency={}",
                    event.getEventId(), event.getType(), event.getAmount(), event.getCurrency());
            metricsService.increment("events.accepted");
            return saved;

        } finally {
            MDC.clear();
        }
    }

    public Event getEvent(String eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public List<Event> getEventsForAccount(String accountId) {
        // Returns sorted chronologically by EventRepository
        return eventRepository.findByAccountId(accountId);
    }
}
