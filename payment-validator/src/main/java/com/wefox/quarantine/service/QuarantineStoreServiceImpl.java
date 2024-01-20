package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.repository.PaymentErrorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class QuarantineStoreServiceImpl implements QuarantineStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarantineStoreServiceImpl.class);

    private final PaymentErrorDataRepository dataRepository;

    @Autowired
    public QuarantineStoreServiceImpl(PaymentErrorDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public Payment storePayment(Payment payment) {
        dataRepository.save(payment);
        LOGGER.info("Store Payment into Quarantine:" + payment.getPaymentId());
        return payment;
    }
}


