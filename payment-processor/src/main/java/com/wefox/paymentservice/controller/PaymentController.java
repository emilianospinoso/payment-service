package com.wefox.paymentservice.controller;


import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payments")
    public String displayPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payments"; // Check if this view name is correct
    }
}