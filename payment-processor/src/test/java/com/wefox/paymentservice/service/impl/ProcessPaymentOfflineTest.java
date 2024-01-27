package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.StoreLogsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        UUID existingPaymentId = UUID.randomUUID();
        Payment payment = new Payment();
   //     payment.setPaymentId(existingPaymentId);
     //   when(dataRepository.existsById(existingPaymentId)).thenReturn(true);
        paymentProcessor.processPayment(payment);
        verify(dataRepository, times(1)).save(payment);
        verify(storeLogsService, never()).sendLogsToDefaultSystem(any());
    }

    @Test
    public void testProcessPaymentForNewPayment() {
        UUID newPaymentId = UUID.randomUUID();
        Payment payment = new Payment();
      //  payment.setPaymentId(newPaymentId);
        // when(dataRepository.existsById(newPaymentId)).thenReturn(false);
        paymentProcessor.processPayment(payment);
        verify(dataRepository, times(1)).save(payment);
        verify(storeLogsService, never()).sendLogsToDefaultSystem(any());
    }
}
