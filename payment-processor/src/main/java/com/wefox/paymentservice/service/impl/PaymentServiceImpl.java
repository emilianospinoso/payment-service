package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.configuration.PaymentSpecifications;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentCriteria;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDataRepository paymentDataRepository;

    @Autowired
    public PaymentServiceImpl(PaymentDataRepository paymentDataRepository) {
        this.paymentDataRepository = paymentDataRepository;
    }

    @Override
    public List<Payment> getPaymentsByCriteria(PaymentCriteria criteria) {
        Specification<Payment> specification = Specification.where(null);

        if (criteria.getAccountId() != null) {
            specification = specification.and(PaymentSpecifications.accountIdEquals(criteria.getAccountId()));
            log.info("Added condition: accountIdEquals, accountId={}", criteria.getAccountId());
        }

        if (criteria.getPaymentType() != null) {
            specification = specification.and(PaymentSpecifications.paymentTypeEquals(criteria.getPaymentType()));
            log.info("Added condition: paymentTypeEquals, paymentType={}", criteria.getPaymentType());
        }

        if (criteria.getAmountGreaterThan() != null) {
            specification = specification.and(PaymentSpecifications.amountGreaterThan(criteria.getAmountGreaterThan()));
            log.info("Added condition: amountGreaterThan, amount={}", criteria.getAmountGreaterThan());
        }

        List<Payment> payments = paymentDataRepository.findAll(specification);
        log.info("Querying payments with criteria: {}", criteria);
        log.info("Found {} payments matching the criteria.", payments.size());

        return payments;
    }
}
