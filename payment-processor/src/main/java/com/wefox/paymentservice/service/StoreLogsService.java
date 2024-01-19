package com.wefox.paymentservice.service;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentError;

public interface StoreLogsService {
    void sendLogsToDefaultStorage(Payment payment);

}