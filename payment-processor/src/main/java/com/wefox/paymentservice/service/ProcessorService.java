package com.wefox.paymentservice.service;

import com.wefox.paymentservice.model.Payment;
import org.springframework.stereotype.Service;


public interface ProcessorService {
    void processPayment(Payment payment);
}