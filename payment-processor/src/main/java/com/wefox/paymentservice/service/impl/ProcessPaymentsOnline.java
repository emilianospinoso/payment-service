package com.wefox.paymentservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.ProcessorService;
import com.wefox.paymentservice.service.StoreLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.atomic.AtomicInteger;

public class ProcessPaymentsOnline implements ProcessorService {
    private final PaymentDataRepository dataRepository;
    private final RestOperations restOperations;
    private final ObjectMapper objectMapper;

    private final StoreLogsService storeLogsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPaymentsOnline.class);

    private static final int MAX_RETRIES = 3;

    private final PaymentAndLogToQuarantine paymentAndLogToQuarantine;

    public ProcessPaymentsOnline(PaymentDataRepository dataRepository, RestOperations restOperations, PaymentAndLogToQuarantine paymentAndLogToQuarantine) {
        this.dataRepository = dataRepository;
        this.restOperations = restOperations;
        this.objectMapper = new ObjectMapper();  // Initialize ObjectMapper
        this.storeLogsService = new SaveLogsIntoSystem(restOperations, paymentAndLogToQuarantine);
        this.paymentAndLogToQuarantine = paymentAndLogToQuarantine;
    }

    @Override
    public void processPayment(Payment payment) {
        try {
            ResponseEntity<String> response = sendRequestWithRetry(payment);
            if (response.getStatusCode().is2xxSuccessful()) {
                boolean paymentExists = dataRepository.existsById(payment.getPaymentId());

                if (paymentExists) {
                    dataRepository.save(payment);
                    LOGGER.info("Payment updated successfully.");
                } else {
                    dataRepository.save(payment);
                    LOGGER.info("New payment processed successfully.");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error trying to process Payment Online: " + payment.getPaymentId() + " " + e);
            storeLogsService.sendLogsToDefaultSystem(payment);
        }
    }

    private ResponseEntity<String> sendRequestWithRetry(Payment payment) {
        AtomicInteger retryCount = new AtomicInteger(0);

        while (retryCount.get() < MAX_RETRIES) {
            try {
                LOGGER.info("Sending request. Retry count: {}", retryCount.get());

                String paymentJson = objectMapper.writeValueAsString(payment);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<>(paymentJson, headers);
                ResponseEntity<String> response = restOperations.exchange(
                        "http://localhost:9000/payment",
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    return response; // Successful response, exit the loop
                } else {
                    retryCount.incrementAndGet();
                }
            } catch (HttpServerErrorException e) {
                handleServerError(e);
                retryCount.incrementAndGet();
            } catch (RestClientException e) {
                LOGGER.error("RestClientException. Retrying...", e);
                retryCount.incrementAndGet();
            } catch (Exception e) {
                LOGGER.error("Error during JSON serialization: " + e);
                retryCount.incrementAndGet();
            }
        }
        // If max retries reached, send to quarantine
        paymentAndLogToQuarantine.sendPaymentToQuarantine(payment);
        throw new RuntimeException("Max retries reached. Unable to get a successful response.");
    }

    private void handleServerError(HttpServerErrorException e) {
        if (e.getRawStatusCode() == 504) {
            LOGGER.warn("Gateway Timeout (504) encountered. Retrying...");
        } else {
            LOGGER.error("HTTP Server Error. Retrying...", e);
        }
    }
}
