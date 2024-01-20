package com.wefox.quarantine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.service.QuarantineStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuarantineControllerTest {

    @Mock
    private QuarantineStoreService quarantineStoreService;

    @InjectMocks
    private QuarantineController quarantineController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(quarantineController).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testPublishPayment() throws Exception {
        // Mock Payment object
        Payment mockPayment = new Payment();
        UUID paymentId = UUID.randomUUID();
        mockPayment.setPaymentId(paymentId);
        mockPayment.setAccountId(123); // Set other properties as needed

        // Mock the behavior of the quarantineStoreService
        Mockito.when(quarantineStoreService.storePayment(Mockito.any(Payment.class))).thenReturn(mockPayment);

        // Perform the HTTP POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/paymenttoquarantine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockPayment)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Payment saved in quarantine. Processed successfully."));

        // Verify that the storePayment method was called with the correct Payment object
        Mockito.verify(quarantineStoreService, Mockito.times(1)).storePayment(Mockito.eq(mockPayment));

        // Additional verification if needed
        assertEquals(paymentId, mockPayment.getPaymentId());
    }
}