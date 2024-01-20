package com.wefox.paymentservice.repository;

import com.wefox.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentDataRepository extends JpaRepository<Payment, UUID> {
}
