package com.wefox.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments_processed")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public final class Payment {
    @Id
    @JsonProperty("payment_id")
    private final String paymentId;

    @JsonProperty("account_id")
    private final int accountId;

    @JsonProperty("payment_type")
    private final String paymentType;

    @JsonProperty("credit_card")
    private final String creditCard;

    private final int amount;

    private final int delay;

    public Payment withAmount(int newAmount) {
        return new Payment(paymentId, accountId, paymentType, creditCard, newAmount, delay);
    }

    public Payment withDelay(int newDelay) {
        return new Payment(paymentId, accountId, paymentType, creditCard, amount, newDelay);
    }
}
