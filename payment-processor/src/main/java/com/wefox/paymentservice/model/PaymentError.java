package com.wefox.paymentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "payment_errors")
public class PaymentError {
    @Id
    private String id;

    private String errorType;
    private String errorDescription;

}
