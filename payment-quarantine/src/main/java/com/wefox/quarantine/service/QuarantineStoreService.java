package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.model.PaymentError;

public interface QuarantineStoreService {
    Payment storePayment(Payment payment);
    PaymentError storeErrorLog(PaymentError paymentError);
}