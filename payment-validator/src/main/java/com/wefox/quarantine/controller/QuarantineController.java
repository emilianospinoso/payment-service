package com.wefox.quarantine.controller;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.service.PaymentRetryService;
import com.wefox.quarantine.service.QuarantineService;
import com.wefox.quarantine.service.QuarantineStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping()
@Controller
public class QuarantineController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarantineController.class);

    private final QuarantineStoreService quarantineStoreService;
    private final QuarantineService quarantineService;
    private final PaymentRetryService paymentRetryService;



    @Autowired
    public QuarantineController(QuarantineStoreService quarantineStoreService,
                                QuarantineService quarantineService,
                                PaymentRetryService paymentRetryService) {
        this.quarantineStoreService = quarantineStoreService;
        this.quarantineService = quarantineService;
        this.paymentRetryService = paymentRetryService;
    }

    @PostMapping("/paymenttoquarantine")
    public ResponseEntity<String> publishPayment(@RequestBody Payment payment) {
        LOGGER.info("Saving Payment to quarantine");
        quarantineStoreService.storePayment(payment);
        return ResponseEntity.ok("Payment saved in quarantine. Processed successfully.");
    }


    @GetMapping("/paymentsinquarantine")
    public String displayPayments(Model model) {
        List<Payment> payments = quarantineService.getAllPayments();
        model.addAttribute("payments", payments);
        return "quarantine";
    }

    @PostMapping("/quarantine/execute-payment-retry")
    public ResponseEntity<String> executePaymentRetry() {
        try {
            paymentRetryService.processPaymentsAgain();
            LOGGER.info("Trying to excecute retry payments.");
            return ResponseEntity.ok("Payment retry executed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Failed to execute payment retry. Error: " + e.getMessage());
        }
    }
}