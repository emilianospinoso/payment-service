package com.wefox.paymentservice.model;

import lombok.Data;

@Data
public class PaymentCriteria {

    private Integer accountId;
    private String paymentType;
    private Integer amountGreaterThan;
}
