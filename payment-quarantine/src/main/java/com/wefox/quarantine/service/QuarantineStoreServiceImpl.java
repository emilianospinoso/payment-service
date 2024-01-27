package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.model.PaymentError;
import com.wefox.quarantine.repository.PaymentErrorDataRepository;
import com.wefox.quarantine.repository.PaymentErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuarantineStoreServiceImpl implements QuarantineStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarantineStoreServiceImpl.class);

    private final PaymentErrorDataRepository paymentDataRepository;
    private final PaymentErrorLogRepository errorLogRepository;

    @Autowired
    public QuarantineStoreServiceImpl(PaymentErrorDataRepository paymentDataRepository, PaymentErrorLogRepository errorLogRepository) {
        this.paymentDataRepository = paymentDataRepository;
        this.errorLogRepository = errorLogRepository;
    }

    @Override
    public Payment storePayment(Payment payment) {
        paymentDataRepository.save(payment);
        LOGGER.info("Store Payment into Quarantine:" + payment.getPaymentId());
        return payment;
    }

    @Override
    public PaymentError storeErrorLog(PaymentError paymentError) {
        LOGGER.error("El id: "+paymentError.getId());
        LOGGER.error("El errorType: "+paymentError.getErrorType());
        LOGGER.error("El errorDescription: "+paymentError.getId());
        errorLogRepository.save(paymentError);
        LOGGER.info("Store Log into Quarantine:" + paymentError.getId());
        return paymentError;
    }
}