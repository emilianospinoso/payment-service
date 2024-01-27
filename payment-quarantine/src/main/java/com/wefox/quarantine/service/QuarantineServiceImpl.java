package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.model.PaymentError;
import com.wefox.quarantine.repository.PaymentErrorDataRepository;
import com.wefox.quarantine.repository.PaymentErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuarantineServiceImpl implements QuarantineService {
    private final PaymentErrorDataRepository paymentRepository;
    private final PaymentErrorLogRepository paymentErrorLogRepository;

    @Autowired
    public QuarantineServiceImpl(PaymentErrorDataRepository paymentRepository,
                                 PaymentErrorLogRepository paymentLogRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentErrorLogRepository = paymentLogRepository;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<PaymentError> getAllPaymentErrorLogs() {
        return paymentErrorLogRepository.findAll();
    }
}