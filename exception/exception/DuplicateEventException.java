
package com.example.gateway.exception;

import com.example.gateway.Model.Event;

public class DuplicateEventException extends RuntimeException {
    private final Event existingEvent;

    public DuplicateEventException(Event existingEvent) {
        super("Duplicate event: " + existingEvent.getEventId());
        this.existingEvent = existingEvent;
    }

    public Event getExistingEvent() { return existingEvent; }
}
