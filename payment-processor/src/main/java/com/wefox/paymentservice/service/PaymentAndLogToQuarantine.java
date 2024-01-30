package com.wefox.paymentservice.service;

import com.wefox.paymentservice.configuration.QuarantineServiceConfig;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentError;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
        restTemplate.postForObject(paymentQuarantineApiUrl, payment, Boolean.class);
        log.error(payment.getPaymentId() + " Payment was moved to quarantine for future analysis");
    }

    public void sendLogToQuarantine(PaymentError paymentError) {
        restTemplate.postForObject(logToQuarantineApiUrl, paymentError, Boolean.class);
        log.error(paymentError.getId() + " Log was moved to quarantine for send to logSystem in the future");
    }

}
