package com.wefox.quarantine.controller;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.service.QuarantineStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping()
public class QuarantineController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarantineController.class);

    private final QuarantineStoreService quarantineStoreService;

    @Autowired
    public QuarantineController(QuarantineStoreService quarantineStoreService) {
        this.quarantineStoreService = quarantineStoreService;
    }

    @PostMapping("/paymenttoquarantine")
    public ResponseEntity<String> publishPayment(@RequestBody Payment payment) {
        LOGGER.info("Saving Payment to quarantine");
        quarantineStoreService.storePayment(payment);
        return ResponseEntity.ok("Payment saved in quarantine. Processed successfully.");
    }

}