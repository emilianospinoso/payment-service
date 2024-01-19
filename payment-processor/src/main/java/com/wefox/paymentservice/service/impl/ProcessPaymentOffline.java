package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessPaymentOffline implements ProcessorService {
    private final PaymentDataRepository dataRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(ProcessPaymentOffline.class);

    public ProcessPaymentOffline(PaymentDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public void processPayment(Payment payment) {
        dataRepository.save(payment);
        LOGGER.info("Offline payment processed successfully.");
    }
}
