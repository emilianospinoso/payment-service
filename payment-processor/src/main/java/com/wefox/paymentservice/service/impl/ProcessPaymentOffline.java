package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.ProcessorService;
import com.wefox.paymentservice.service.StoreLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

public class ProcessPaymentOffline implements ProcessorService {
    private final PaymentDataRepository dataRepository;
    private final RestOperations restOperations;

    private final StoreLogsService storeLogsService;
    private final Logger LOGGER = LoggerFactory.getLogger(ProcessPaymentOffline.class);

    public ProcessPaymentOffline(PaymentDataRepository dataRepository, RestOperations restOperations, PaymentAndLogToQuarantine paymentAndLogToQuarantine) {
        this.dataRepository = dataRepository;
        this.restOperations = restOperations;
        this.storeLogsService = new SaveLogsIntoSystem(restOperations, paymentAndLogToQuarantine);
    }


    @Override
    public void processPayment(Payment payment) {
        try {
            if (payment.getPaymentId() != null && dataRepository.existsById(payment.getPaymentId())) {
                LOGGER.info("Updating existing payment with paymentId: {}", payment.getPaymentId());
                dataRepository.save(payment);
            } else {
                LOGGER.info("Saving new payment with paymentId: {}", payment.getPaymentId());
                dataRepository.save(payment);
            }
            LOGGER.info("Offline payment processed successfully.");
        } catch (Exception e) {
            LOGGER.error("Error processing payment with paymentId: {}. Sending logs to the default system.", payment.getPaymentId(), e);
            // Send logs to the default system
            storeLogsService.sendLogsToDefaultSystem(payment);
        }
    }
}
