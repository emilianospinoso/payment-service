package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.StoreLogsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestOperations;

import static org.mockito.Mockito.*;

class ProcessPaymentsOnlineTest {

    @InjectMocks
    private ProcessPaymentsOnline paymentProcessor;

    @Mock
    private PaymentDataRepository dataRepository;

    @Mock
    private RestOperations restOperations;

    @Mock
    private PaymentAndLogToQuarantine paymentAndLogToQuarantine;

    @Mock
    private StoreLogsService storeLogsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPaymentSuccessful() {
        // Arrange
        Payment payment = new Payment();

        ResponseEntity<String> successResponse = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(successResponse);

        // Act
        paymentProcessor.processPayment(payment);

        // Assert
        verify(dataRepository, times(1)).existsById(any());
        verify(dataRepository, times(1)).save(payment);
        verify(storeLogsService, never()).sendLogsToDefaultSystem(any());
        verify(paymentAndLogToQuarantine, never()).sendPaymentToQuarantine(any());
    }

    @Test
    void testProcessPaymentWithServerError() {
        // Arrange
        Payment payment = new Payment();
        when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        // Act
        paymentProcessor.processPayment(payment);
        // Assert
        verify(paymentAndLogToQuarantine, times(1)).sendPaymentToQuarantine(payment);
    }

}