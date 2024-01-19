package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.repository.PaymentErrorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentValidationServiceImpl implements QuarantineStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentValidationServiceImpl.class);

    private final PaymentErrorDataRepository dataRepository;

    @Autowired
    public PaymentValidationServiceImpl(PaymentErrorDataRepository dataRepo, PaymentErrorDataRepository dataRepository) {
        this.dataRepository = dataRepo;
    }

    @Override
    public Payment storePayment(Payment payment) {
        dataRepository.save(payment);
        LOGGER.info("Store in Quarantine:" + payment.getPaymentId());
        return payment;
    }

}
