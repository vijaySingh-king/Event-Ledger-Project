package com.exmaple.gateway.repo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.example.gateway.Model.Event;



@Repository
public class EventRepository {

    private final Map<String, Event> store = new ConcurrentHashMap<>();

    public Optional<Event> findById(String eventId) {
        return Optional.ofNullable(store.get(eventId));
    }

    public Event save(Event event) {
        store.put(event.getEventId(), event);
        return event;
    }

    public List<Event> findByAccountId(String accountId) {
        return store.values().stream()
                .filter(e -> accountId.equals(e.getAccountId()))
                .sorted(Comparator.comparing(Event::getEventTimestamp))
                .toList();
    }

    /** Used in tests to reset state between test runs */
    public void clear() {
        store.clear();
    }
}
