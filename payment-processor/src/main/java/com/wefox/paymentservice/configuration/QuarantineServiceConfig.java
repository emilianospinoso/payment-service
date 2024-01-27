package com.wefox.paymentservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuarantineServiceConfig {

    @Value("${quarantine.payment.url}")
    private String paymentQuarantineApiUrl;

    @Value("${quarantine.log.url}")
    private String logToQuarantineApiUrl;

    public String getPaymentQuarantineApiUrl() {
        return paymentQuarantineApiUrl;
    }

    public String getLogToQuarantineApiUrl() {
        return logToQuarantineApiUrl;
    }
}
