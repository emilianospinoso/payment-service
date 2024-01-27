package com.wefox.paymentservice.controller;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payments")
    public String displayPayments(
            Model model,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String paymentType
    ) {
        List<Payment> payments;

        if (accountId != null && !accountId.isEmpty()) {
            int accountIdInt = Integer.parseInt(accountId);
            payments = paymentService.getPaymentsByAccountId(accountIdInt);

            if (paymentType != null && !paymentType.isEmpty()) {
                payments = paymentService.getPaymentsByAccountIdAndType(accountIdInt, paymentType);
            }
        } else {
            payments = paymentService.getAllPayments();

            if (paymentType != null && !paymentType.isEmpty()) {
                // Fetch all payments and then filter by paymentType
                payments = filterPaymentsByType(payments, paymentType);
            }
        }

        model.addAttribute("payments", payments);
        return "payments";
    }

    private List<Payment> filterPaymentsByType(List<Payment> payments, String paymentType) {
        // Implement the logic to filter payments by paymentType
        // You might want to iterate through the list and keep only the matching payments

        List<Payment> filteredPayments = new ArrayList<>();

        for (Payment payment : payments) {
            if (paymentType.equalsIgnoreCase(payment.getPaymentType())) {
                filteredPayments.add(payment);
            }
        }

        return filteredPayments;
    }
}