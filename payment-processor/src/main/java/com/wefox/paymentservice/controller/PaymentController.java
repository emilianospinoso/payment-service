package com.wefox.paymentservice.controller;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentCriteria;
import com.wefox.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Slf4j
@Controller
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payments")
    public String displayPayments(Model model, @ModelAttribute PaymentCriteria criteria) {
        List<Payment> payments = paymentService.getPaymentsByCriteria(criteria);
        model.addAttribute("payments", payments);
        model.addAttribute("criteria", criteria);
        return "payments";
    }
}
