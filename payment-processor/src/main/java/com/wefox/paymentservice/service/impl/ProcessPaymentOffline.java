package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.ProcessorService;
import com.wefox.paymentservice.service.StoreLogsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

@Slf4j
public class ProcessPaymentOffline implements ProcessorService {
    private final PaymentDataRepository dataRepository;
    private final StoreLogsService storeLogsService;

    public ProcessPaymentOffline(PaymentDataRepository dataRepository, RestOperations restOperations, PaymentAndLogToQuarantine paymentAndLogToQuarantine) {
        this.dataRepository = dataRepository;
        this.storeLogsService = new SaveLogsIntoSystem(restOperations, paymentAndLogToQuarantine);
    }

    public void processPayment(Payment payment) {
        try {
            if (payment.getPaymentId() != null && dataRepository.existsById(payment.getPaymentId())) {
                log.info("Updating existing payment with paymentId: {}", payment.getPaymentId());
                dataRepository.save(payment);
            } else {
                log.info("Saving new payment with paymentId: {}", payment.getPaymentId());
                dataRepository.save(payment);
            }
            log.info("Offline payment processed successfully.");
        } catch (Exception e) {
            log.error("Error processing payment with paymentId: {}. Sending logs to the default system.", payment.getPaymentId(), e);
            // Send logs to the default system
            storeLogsService.sendLogsToDefaultSystem(payment);
        }
    }
}
