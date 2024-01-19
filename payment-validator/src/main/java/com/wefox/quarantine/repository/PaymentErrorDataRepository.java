package com.wefox.quarantine.repository;

import com.wefox.quarantine.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentErrorDataRepository extends JpaRepository<Payment, Long> {
}
