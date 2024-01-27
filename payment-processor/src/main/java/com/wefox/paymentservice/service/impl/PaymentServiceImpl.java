package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDataRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentDataRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getPaymentsByAccountId(int accountId) {
        return paymentRepository.findByAccountId(accountId);
    }

    @Override
    public List<Payment> getPaymentsByAccountIdAndType(int accountId, String paymentType) {
        return paymentRepository.findByAccountIdAndPaymentType(accountId, paymentType);
    }
}