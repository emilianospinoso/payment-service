package com.wefox.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments_processed")
@Data
@NoArgsConstructor
public class Payment {
    @Id
    @JsonProperty("payment_id")
    private String paymentId; // Change the type to String for UUID

    @JsonProperty("account_id")
    private int accountId;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("credit_card")
    private String creditCard;

    private int amount;
    private int delay;
}
