package com.wefox.paymentservice.repository;

import com.wefox.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentDataRepository extends JpaRepository<Payment, String>, JpaSpecificationExecutor<Payment> {
}
