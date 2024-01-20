package com.wefox.paymentservice.service;

import com.wefox.paymentservice.model.Payment;

public interface StoreLogsService {
    void sendLogsToDefaultSystem(Payment payment);

}