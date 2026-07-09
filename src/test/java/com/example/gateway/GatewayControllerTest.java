package com.example.gateway;



import com.example.gateway.Model.Event;
import com.example.gateway.controller.GatewayController;
import com.example.gateway.service.GatewayService;
import com.example.gateway.service.MetricsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GatewayController.class)
public class GatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GatewayService gatewayService;

    @MockitoBean
    private MetricsService metricsService;

    @Test
    public void testSubmitEvent_Success() throws Exception {
        String json = """
            {
                "eventId": "evt_1",
                "accountId": "acc_1",
                "type": "CREDIT",
                "amount": 100.0,
                "currency": "USD",
                "eventTimestamp": "2026-07-09T10:00:00Z"
            }
            """;

        Event event = new Event();
        event.setEventId("evt_1");
        
        when(gatewayService.submitEvent(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value("evt_1"));
    }
}