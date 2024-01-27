package com.wefox.paymentservice.service;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentCriteria;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllPayments();

    List<Payment> getPaymentsByCriteria(PaymentCriteria criteria);

}