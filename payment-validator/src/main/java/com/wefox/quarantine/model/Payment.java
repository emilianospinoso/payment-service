package com.wefox.quarantine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Payment {
    @Id
    @JsonProperty("payment_id")
    private Long paymentId;

    @JsonProperty("account_id")
    private int accountId;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("credit_card")
    private String creditCard;

    private int amount;
    private int delay;
}
