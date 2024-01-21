package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.repository.PaymentErrorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuarantineServiceImpl implements QuarantineService {
    private final PaymentErrorDataRepository paymentRepository;

    @Autowired
    public QuarantineServiceImpl(PaymentErrorDataRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}