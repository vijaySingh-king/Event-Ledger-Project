package com.example.gateway.Model;

import com.fasterxml.jackson.annotation.JsonInclude;

import ch.qos.logback.core.spi.ConfigurationEvent.EventType;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
    private String eventId;
    private String accountId;
    private EventType type;
    private double amount;
    private String currency;
    private Instant eventTimestamp;
    private Map<String, Object> metadata;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(Instant ts) { this.eventTimestamp = ts; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
	public void setType(com.example.gateway.Model.EventType credit) {
		// TODO Auto-generated method stub
		
	}
}
