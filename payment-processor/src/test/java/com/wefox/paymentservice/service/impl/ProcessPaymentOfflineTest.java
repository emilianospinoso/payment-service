package com.wefox.paymentservice.service.impl;

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
import org.springframework.web.client.RestOperations;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentOfflineTest {

    @Mock
    private PaymentDataRepository dataRepository;

    @Mock
    private RestOperations restOperations;

    @Mock
    private PaymentAndLogToQuarantine paymentAndLogToQuarantine;

    @Mock
    private StoreLogsService storeLogsService;

    @InjectMocks
    private ProcessPaymentOffline processPaymentOffline;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        // Initialize test payment
        testPayment = new Payment("testId", 1, "type", "card", 100, 0);
    }

    @Test
    void processPayment_NewPayment_SaveToRepository() {
        // Arrange
        when(dataRepository.existsById(anyString())).thenReturn(false);

        // Act
        processPaymentOffline.processPayment(testPayment);

        // Assert
        verify(dataRepository, times(1)).save(testPayment);

    }

    @Test
    void processPayment_ExistingPayment_UpdateRepository() {
        // Arrange
        when(dataRepository.existsById(anyString())).thenReturn(true);

        // Act
        processPaymentOffline.processPayment(testPayment);

        // Assert
        verify(dataRepository, times(1)).save(testPayment);
    }

    @Test
    void processPayment_Exception_SendLogsToDefaultSystem() {
        // Arrange
        when(dataRepository.existsById(anyString())).thenThrow(new RuntimeException("Simulated exception"));

        // Act
        processPaymentOffline.processPayment(testPayment);

        // Assert
        verify(storeLogsService, times(1)).sendLogsToDefaultSystem(testPayment);

    }
}
