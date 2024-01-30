package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentError;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.StoreLogsService;
import com.wefox.paymentservice.util.PaymentConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.util.Map;

@Slf4j
public class SaveLogsIntoSystem implements StoreLogsService {
    private static final String LOG_URL = "http://localhost:9000/log";

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
                    LOG_URL, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Logs stored successfully for payment_id: {}", payment.getPaymentId());
            } else {
                // If connection with the default logger service fails, send the payment to quarantine
                log.error("Failed to store logs. HTTP status code: {}", response.getStatusCodeValue());
                paymentAndLogToQuarantine.sendLogToQuarantine(paymentError);
            }
        } catch (RestClientException e) {
            log.error("Error sending logs to storage", e);
        }
    }
}