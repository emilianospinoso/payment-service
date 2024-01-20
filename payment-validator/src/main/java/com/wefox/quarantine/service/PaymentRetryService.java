package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;

import java.util.List;
public interface PaymentRetryService {
    List<Payment> getAllPayments();

    void processPaymentsAgain();
}