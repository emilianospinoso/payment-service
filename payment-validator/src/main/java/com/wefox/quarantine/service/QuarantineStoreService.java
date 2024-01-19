package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;

public interface QuarantineStoreService {
    Payment storePayment(Payment payment);
}