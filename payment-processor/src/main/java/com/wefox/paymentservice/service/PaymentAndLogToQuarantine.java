package com.wefox.paymentservice.service;

import com.wefox.paymentservice.configuration.QuarantineServiceConfig;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class PaymentAndLogToQuarantine {
    private final RestTemplate restTemplate;
    private final String paymentQuarantineApiUrl;
    private final String logToQuarantineApiUrl;

    public PaymentAndLogToQuarantine(RestOperations restOperations, QuarantineServiceConfig config) {
        this.restTemplate = new RestTemplate();
        this.paymentQuarantineApiUrl = config.getPaymentQuarantineApiUrl();
        this.logToQuarantineApiUrl = config.getLogToQuarantineApiUrl();
    }

    public void sendPaymentToQuarantine(Payment payment) {
        try {
            restTemplate.postForObject(paymentQuarantineApiUrl, payment, Boolean.class);
            log.error(payment.getPaymentId() + " Payment was moved to quarantine for future analysis");
        } catch (RestClientException ex) {
            log.error("Error sending payment to quarantine: " + ex.getMessage());
        }
    }

    public void sendLogToQuarantine(PaymentError paymentError) {
        try {
            restTemplate.postForObject(logToQuarantineApiUrl, paymentError, Boolean.class);
            log.error(paymentError.getId() + " Log was moved to quarantine for send to logSystem in the future");
        } catch (RestClientException ex) {
            log.error("Error sending log to quarantine: " + ex.getMessage());
        }
    }
}
