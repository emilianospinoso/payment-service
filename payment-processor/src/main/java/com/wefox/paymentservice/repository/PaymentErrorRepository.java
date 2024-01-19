package com.wefox.paymentservice.repository;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentErrorRepository extends JpaRepository<PaymentError, Long> {
}
