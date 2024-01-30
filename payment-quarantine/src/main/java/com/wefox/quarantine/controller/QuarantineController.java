package com.wefox.quarantine.controller;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.model.PaymentError;
import com.wefox.quarantine.service.PaymentRetryService;
import com.wefox.quarantine.service.QuarantineService;
import com.wefox.quarantine.service.QuarantineStoreService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequestMapping()
@Controller
public class QuarantineController {
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
        log.info("Saving Payment to quarantine");
        quarantineStoreService.storePayment(payment);
        return ResponseEntity.ok("Payment saved in quarantine. Processed successfully.");
    }

    @PostMapping("/logtoquarantine")
    public ResponseEntity<String> publishErrorLog(@RequestBody PaymentError paymentError) {
        log.info("Saving Error log to quarantine");
        quarantineStoreService.storeErrorLog(paymentError);
        return ResponseEntity.ok("Error log saved in quarantine. Processed successfully.");
    }

    @GetMapping("/paymentsinquarantine")
    public String displayPayments(Model model) {
        List<Payment> payments = quarantineService.getAllPayments();
        model.addAttribute("payments", payments);
        return "quarantinepayments";
    }

    @GetMapping("/qarantinelogs")
    public String displayLogsInQuarantine(Model model) {
        List<PaymentError> errors = quarantineService.getAllPaymentErrorLogs();
        model.addAttribute("errors", errors);
        return "quarantinelogs";
    }

    @PostMapping("/quarantine/execute-payment-retry")
    public ResponseEntity<String> executePaymentRetry() {
        try {
            paymentRetryService.processPaymentsAgain();
            log.info("Trying to excecute retry payments.");
            return ResponseEntity.ok("Payment retry executed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Failed to execute payment retry. Error: " + e.getMessage());
        }
    }
}