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
    private final ObjectMapper objectMapper;  // Added ObjectMapper

    private final StoreLogsService storeLogsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPaymentsOnline.class);

    // Adjust maxRetries according to your needs
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
                // Check if payment with the same ID already exists
                boolean paymentExists = dataRepository.existsById(payment.getPaymentId());

                if (paymentExists) {
                    // Update the existing payment
                    dataRepository.save(payment);
                    LOGGER.info("Payment updated successfully.");
                } else {
                    // Save a new payment
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
        int maxRetries = 5;
        AtomicInteger retryCount = new AtomicInteger(0);

        while (retryCount.get() < maxRetries) {
            try {
                LOGGER.info("Sending request. Retry count: {}", retryCount.get());

                // Convert Payment object to JSON string
                String paymentJson = objectMapper.writeValueAsString(payment);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Set headers in the request
                HttpEntity<String> requestEntity = new HttpEntity<>(paymentJson, headers);

                // Send the request with headers
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
                // Handle server errors
                handleServerError(e);
                retryCount.incrementAndGet();
            } catch (RestClientException e) {
                // Handle RestClientException
                handleRestClientException(e);
                retryCount.incrementAndGet();
            } catch (Exception e) {
                // Handle other exceptions
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

    private void handleRestClientException(RestClientException e) {
        LOGGER.error("RestClientException. Retrying...", e);
    }
}
