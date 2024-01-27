package com.wefox.paymentservice.repository;

import com.wefox.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentDataRepository extends JpaRepository<Payment, String> {
    List<Payment> findByAccountId(int accountId);

    List<Payment> findByAccountIdAndPaymentType(int accountId, String paymentType);

}
