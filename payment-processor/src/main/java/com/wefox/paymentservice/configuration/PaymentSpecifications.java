package com.wefox.paymentservice.configuration;

import com.wefox.paymentservice.model.Payment;
import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecifications {

    public static Specification<Payment> accountIdEquals(int accountId) {
        return (root, query, builder) -> builder.equal(root.get("accountId"), accountId);
    }

    public static Specification<Payment> paymentTypeEquals(String paymentType) {
        return (root, query, builder) -> builder.equal(root.get("paymentType"), paymentType);
    }

    public static Specification<Payment> amountGreaterThan(int amount) {
        return (root, query, builder) -> builder.greaterThan(root.get("amount"), amount);
    }
}