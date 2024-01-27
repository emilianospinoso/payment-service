package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentError;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.StoreLogsService;
import com.wefox.paymentservice.util.PaymentConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.Map;

public class SaveLogsIntoSystem implements StoreLogsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveLogsIntoSystem.class);
    private final RestOperations restOperations;
    private final PaymentAndLogToQuarantine paymentAndLogToQuarantine;

    public SaveLogsIntoSystem(RestOperations restOperations, PaymentAndLogToQuarantine paymentAndLogToQuarantine) {
        this.restOperations = restOperations;
        this.paymentAndLogToQuarantine = paymentAndLogToQuarantine;
    }

    private Map<String, Object> createLogPayload(PaymentError paymentError) {
        return Map.of(
                "payment_id", paymentError.getId(),
                "error_type", paymentError.getErrorType(),
                "error_description", paymentError.getErrorDescription()
        );
    }

    @Override
    public void sendLogsToDefaultSystem(Payment payment) {
        try {
            PaymentError paymentError = PaymentConversionUtil.convertToError(payment);
            Map<String, Object> logPayload = createLogPayload(paymentError);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(logPayload, headers);

            ResponseEntity<String> response = restOperations.postForEntity(
                    "http://localhost:9000/log", requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Logs stored successfully.");
            } else {
                // If connection with the default logger service fails, send the payment to quarantine
                LOGGER.error("Failed to store logs. HTTP status code: {}", response.getStatusCodeValue());
                LOGGER.error("EL PAYMENTERROR QUE MANDO ES : " + paymentError.getId());
                LOGGER.error("lA descripction QUE MANDO ES : " + paymentError.getErrorDescription());
                paymentAndLogToQuarantine.sendLogToQuarantine(paymentError);
            }
        } catch (Exception e) {
            LOGGER.error("Error sending logs to storage", e);
        }
    }
}
