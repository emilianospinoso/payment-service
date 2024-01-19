package com.wefox.paymentservice.repository;

import com.wefox.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDataRepository extends JpaRepository<Payment, Long> {
}
