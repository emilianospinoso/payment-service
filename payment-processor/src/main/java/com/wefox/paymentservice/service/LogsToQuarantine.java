package com.wefox.paymentservice.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class LogsToQuarantine {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogsToQuarantine.class);
    private final RestTemplate restTemplate;
    private final String paymentValidatorApiUrl = "http://localhost:8082/quarantine";

    public LogsToQuarantine(RestOperations restOperations) {
        this.restTemplate = new RestTemplate();
    }

    public void sendToQuarantine(Payment payment) {
        ObjectMapper objectMapper = new ObjectMapper();
        restTemplate.postForObject(paymentValidatorApiUrl, payment, Boolean.class);
        LOGGER.error(payment.getPaymentId() + " was moved to quarantine");
    }
}
