package com.wefox.paymentservice.service;

import com.wefox.paymentservice.model.Payment;


public interface ProcessorService {
    void processPayment(Payment payment);
}