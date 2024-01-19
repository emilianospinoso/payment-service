package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentError;
import com.wefox.paymentservice.service.LogsToQuarantine;
import com.wefox.paymentservice.service.StoreLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.HashMap;
import java.util.Map;

public class SaveLogsIntoSystem implements StoreLogsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveLogsIntoSystem.class);
    private final RestOperations restOperations;
    private final LogsToQuarantine logsToQuarantine;

    public SaveLogsIntoSystem(RestOperations restOperations, LogsToQuarantine logsToQuarantine) {
        this.restOperations = restOperations;
        this.logsToQuarantine = logsToQuarantine;
    }

    public PaymentError convertToError(Payment payment) {
        // Convert Payment to PaymentError
        PaymentError paymentError = new PaymentError();
        paymentError.setId(String.valueOf(payment.getPaymentId()));
        paymentError.setErrorType(payment.getPaymentType());
        paymentError.setErrorDescription("Error occurred during payment processing");
        return paymentError;
    }

    @Override
    public void sendLogsToDefaultStorage(Payment payment) {
        try {
            PaymentError paymentError = convertToError(payment);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create a Map representing the JSON payload
            Map<String, Object> logPayload = new HashMap<>();
            logPayload.put("payment_id", paymentError.getId());
            logPayload.put("error_type", paymentError.getErrorType());
            logPayload.put("error_description", paymentError.getErrorDescription());
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(logPayload, headers);

            // Use RestOperations to send the HTTP POST request
            ResponseEntity<String> response = restOperations.postForEntity(
                    "http://localhost:9000/log", requestEntity, String.class);

            // Check the response status
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Logs stored successfully.");
            } else {
                // If connection with the default logger service fails, send the payment to quarantine
                LOGGER.error("Failed to store logs. HTTP status code: {}", response.getStatusCodeValue());
                logsToQuarantine.sendToQuarantine(payment);
            }
        } catch (Exception e) {
            LOGGER.error("Error sending logs to storage", e);
        }
    }
}
