package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.model.PaymentError;
import com.wefox.quarantine.repository.PaymentErrorDataRepository;
import com.wefox.quarantine.repository.PaymentErrorLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuarantineStoreServiceImpl implements QuarantineStoreService {
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
        log.info("Store Payment into Quarantine:" + payment.getPaymentId());
        return payment;
    }

    @Override
    public PaymentError storeErrorLog(PaymentError paymentError) {
        errorLogRepository.save(paymentError);
        log.info("Store Log into Quarantine:" + paymentError.getId());
        return paymentError;
    }
}