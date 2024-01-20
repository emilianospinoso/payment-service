package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.StoreLogsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.event.Level;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessPaymentOfflineTest {

    @InjectMocks
    private ProcessPaymentOffline paymentProcessor;

    @Mock
    private PaymentDataRepository dataRepository;

    @Mock
    private StoreLogsService storeLogsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessPaymentForExistingPayment() {
        // Arrange
        UUID existingPaymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setPaymentId(existingPaymentId);

        // Mock behavior
        when(dataRepository.existsById(existingPaymentId)).thenReturn(true);

        // Act
        paymentProcessor.processPayment(payment);

        // Assert
        verify(dataRepository, times(1)).save(payment);
        verify(storeLogsService, never()).sendLogsToDefaultSystem(any());
    }

    @Test
    public void testProcessPaymentForNewPayment() {
        // Arrange
        UUID newPaymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setPaymentId(newPaymentId);

        // Mock behavior
        when(dataRepository.existsById(newPaymentId)).thenReturn(false);

        // Act
        paymentProcessor.processPayment(payment);

        // Assert
        verify(dataRepository, times(1)).save(payment);
        verify(storeLogsService, never()).sendLogsToDefaultSystem(any());
    }
}
