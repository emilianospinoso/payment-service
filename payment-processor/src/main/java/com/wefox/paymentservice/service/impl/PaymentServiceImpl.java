package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.configuration.PaymentSpecifications;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentCriteria;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentDataRepository paymentDataRepository;

    @Override
    public List<Payment> getAllPayments() {
        return paymentDataRepository.findAll();
    }

    @Override
    public List<Payment> getPaymentsByCriteria(PaymentCriteria criteria) {
        Specification<Payment> specification = Specification.where(null);

        if (criteria.getAccountId() != null) {
            specification = specification.and(PaymentSpecifications.accountIdEquals(criteria.getAccountId()));
            LOGGER.info("Added condition: accountIdEquals, accountId={}", criteria.getAccountId());
        }

        if (criteria.getPaymentType() != null) {
            specification = specification.and(PaymentSpecifications.paymentTypeEquals(criteria.getPaymentType()));
            LOGGER.info("Added condition: paymentTypeEquals, paymentType={}", criteria.getPaymentType());
        }

        if (criteria.getAmountGreaterThan() != null) {
            specification = specification.and(PaymentSpecifications.amountGreaterThan(criteria.getAmountGreaterThan()));
            LOGGER.info("Added condition: amountGreaterThan, amount={}", criteria.getAmountGreaterThan());
        }

        List<Payment> payments = paymentDataRepository.findAll(specification);
        LOGGER.info("Querying payments with criteria: {}", criteria);
        LOGGER.info("Found {} payments matching the criteria.", payments.size());

        return payments;
    }
}
