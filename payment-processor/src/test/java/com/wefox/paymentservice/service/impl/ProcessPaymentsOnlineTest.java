package com.wefox.paymentservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.StoreLogsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentsOnlineTest {

    @Mock
    private PaymentDataRepository dataRepository;

    @Mock
    private RestOperations restOperations;

    @Mock
    private StoreLogsService storeLogsService;

    @Mock
    private PaymentAndLogToQuarantine paymentAndLogToQuarantine;

    @InjectMocks
    private ProcessPaymentsOnline processPaymentsOnline;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        // Initialize test payment
        testPayment = new Payment("testId", 1, "type", "card", 100, 0);
    }

    @Test
    void processPayment_SuccessfulRequest_SaveToRepository() {
        // Arrange
        when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

        // Act
        processPaymentsOnline.processPayment(testPayment);

        // Assert
        verify(dataRepository, times(1)).save(testPayment);
    }

    @Test
    void processPayment_UnsuccessfulRequest_RetryAndSendToQuarantine() {
        // Arrange
        when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR))
                .thenReturn(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR))
                .thenReturn(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));

        // Act
        assertThrows(RuntimeException.class, () -> processPaymentsOnline.processPayment(testPayment));

        // Assert
        verify(dataRepository, never()).save(testPayment);
        verify(storeLogsService, times(1)).sendLogsToDefaultSystem(testPayment);
        verify(paymentAndLogToQuarantine, times(1)).sendPaymentToQuarantine(testPayment);
    }

}