package com.example.gateway;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.gateway.Model.Event;
import com.example.gateway.Model.EventType;
import com.example.gateway.client.AccountClient;
import com.example.gateway.service.GatewayService;
import com.exmaple.gateway.repo.EventRepository;

@SpringBootTest
public class GatewayIntegrationTest {

    @Autowired
    private GatewayService gatewayService;

    @Autowired
    private EventRepository eventRepository;

    @MockitoBean
    private AccountClient accountClient;

    @BeforeEach
    void setup() {
        eventRepository.clear();
        when(accountClient.applyTransaction(any(Event.class), anyString()))
                .thenReturn(ResponseEntity.ok("OK"));
    }

    @Test
    public void testFullEventFlow() {
        Event event = new Event();
        event.setEventId("test_1");
        event.setAccountId("acc_1");
        event.setType(EventType.CREDIT);
        event.setAmount(50.0);
        event.setCurrency("INR");
        event.setEventTimestamp(Instant.now());

        // Submit
        gatewayService.submitEvent(event);

        // Verify Persistence
        List<Event> results = gatewayService.getEventsForAccount("acc_1");
        assertEquals(1, results.size());
        assertEquals("test_1", results.get(0).getEventId());
    }
}