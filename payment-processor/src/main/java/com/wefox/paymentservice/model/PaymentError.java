package com.wefox.paymentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Data
@Table(name = "payment_errors")
public class PaymentError {
    @Id
    private String id;
    private String errorType;
    private String errorDescription;

    public PaymentError() {
    }

    public PaymentError(String id, String errorType, String errorDescription) {
        this.id = id;
        this.errorType = errorType;
        this.errorDescription = errorDescription;
    }
}
