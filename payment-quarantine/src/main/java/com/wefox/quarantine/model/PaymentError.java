package com.wefox.quarantine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "error_logs_quarantine")
public class PaymentError {
    @Id
    private String id;

    private String errorType;
    private String errorDescription;

    // Constructors, getters, setters
}