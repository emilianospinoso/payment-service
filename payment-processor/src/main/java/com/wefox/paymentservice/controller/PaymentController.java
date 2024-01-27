package com.wefox.paymentservice.controller;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentCriteria;
import com.wefox.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payments")
    public String displayPayments(Model model, @ModelAttribute PaymentCriteria criteria) {
        logger.info("Received request for payments with criteria: {}", criteria);

        if (criteria.getAccountId() == null && criteria.getPaymentType() == null && criteria.getAmountGreaterThan() == null) {
            criteria = null; // Set criteria to null to retrieve all payments
            logger.info("No filter criteria provided. Retrieving all payments.");
        } else if (criteria.getPaymentType() == null) {
            criteria.setPaymentType(""); // Set paymentType to an empty string if not provided
            logger.info("No paymentType selected. Setting paymentType to an empty string.");
        }

        List<Payment> payments = paymentService.getPaymentsByCriteria(criteria);
        model.addAttribute("payments", payments);
        model.addAttribute("criteria", criteria);

        logger.info("Found {} payments matching the criteria.", payments.size());
        return "payments";
    }
}
