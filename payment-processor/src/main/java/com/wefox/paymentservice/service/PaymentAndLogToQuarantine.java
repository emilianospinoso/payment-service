package com.wefox.paymentservice.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentAndLogToQuarantine {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentAndLogToQuarantine.class);
    private final RestTemplate restTemplate;
    private final String paymentValidatorApiUrl = "http://localhost:8082/paymenttoquarantine";


    public PaymentAndLogToQuarantine(RestOperations restOperations) {
        this.restTemplate = new RestTemplate();
    }

    public void sendLogToQuarantine(Payment payment) {
        restTemplate.postForObject(paymentValidatorApiUrl, payment, Boolean.class);
        LOGGER.error(payment.getPaymentId() + "Log was moved to quarantine");
    }

    public void sendPaymentToQuarantine(Payment payment) {
        restTemplate.postForObject(paymentValidatorApiUrl, payment, Boolean.class);
        LOGGER.error(payment.getPaymentId() + " was moved to quarantine");
    }
}
