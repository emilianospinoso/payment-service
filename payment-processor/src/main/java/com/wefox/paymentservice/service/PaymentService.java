package com.wefox.paymentservice.service;


import com.wefox.paymentservice.model.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllPayments();
}
