package com.wefox.quarantine.repository;

import com.wefox.quarantine.model.PaymentError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentErrorLogRepository extends JpaRepository<PaymentError, String> {
}
