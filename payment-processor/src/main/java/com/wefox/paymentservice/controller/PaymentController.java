package com.wefox.paymentservice.controller;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payments")
    public String displayPayments(
            Model model,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) Integer amountFilter
    ) {
        List<Payment> payments;

        if (accountId != null && !accountId.isEmpty()) {
            int accountIdInt = Integer.parseInt(accountId);
            payments = paymentService.getPaymentsByAccountId(accountIdInt);

            if (paymentType != null && !paymentType.isEmpty()) {
                payments = paymentService.getPaymentsByAccountIdAndType(accountIdInt, paymentType);
            }

            if (amountFilter != null) {
                payments = filterPaymentsByAmount(payments, amountFilter);
            }
        } else {
            payments = paymentService.getAllPayments();

            if (paymentType != null && !paymentType.isEmpty()) {
                payments = filterPaymentsByType(payments, paymentType);
            }

            if (amountFilter != null) {
                payments = filterPaymentsByAmount(payments, amountFilter);
            }
        }

        model.addAttribute("payments", payments);
        return "payments";
    }

    private List<Payment> filterPaymentsByType(List<Payment> payments, String paymentType) {
        return payments.stream()
                .filter(payment -> paymentType.equalsIgnoreCase(payment.getPaymentType()))
                .collect(Collectors.toList());
    }

    private List<Payment> filterPaymentsByAmount(List<Payment> payments, int amountFilter) {
        return payments.stream()
                .filter(payment -> payment.getAmount() >= amountFilter)
                .collect(Collectors.toList());
    }
}