package com.wefox.paymentservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.ProcessorService;
import com.wefox.paymentservice.service.StoreLogsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProcessPaymentsOnlineVT implements ProcessorService {
    private final PaymentDataRepository dataRepository;
    private final RestOperations restOperations;
    private final StoreLogsService storeLogsService;
    private static final int MAX_RETRIES = 3;
    private static final int GATEWAY_TIMEOUT = 504;
    private final PaymentAndLogToQuarantine paymentAndLogToQuarantine;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Executor executor = Executors.newFixedThreadPool(10);

    public ProcessPaymentsOnlineVT(PaymentDataRepository dataRepository, RestOperations restOperations, PaymentAndLogToQuarantine paymentAndLogToQuarantine) {
        this.dataRepository = dataRepository;
        this.restOperations = restOperations;
        this.storeLogsService = new SaveLogsIntoSystem(restOperations, paymentAndLogToQuarantine);
        this.paymentAndLogToQuarantine = paymentAndLogToQuarantine;
    }

    @Override
    public void processPayment(Payment payment) {
        CompletableFuture.runAsync(() -> processPaymentAsync(payment), executor);
    }

    private void processPaymentAsync(Payment payment) {
        try {
            ResponseEntity<String> response = sendRequestWithRetry(payment);
            if (response.getStatusCode().is2xxSuccessful()) {
                synchronized (this) {
                    dataRepository.save(payment);
                }
            }
        } catch (Exception e) {
            log.error("Error trying to process Payment Online: " + payment.getPaymentId(), e);
            storeLogsService.sendLogsToDefaultSystem(payment);
        }
    }

    private ResponseEntity<String> sendRequestWithRetry(Payment payment) {
        AtomicInteger retryCount = new AtomicInteger(0);

        while (retryCount.get() < MAX_RETRIES) {
            try {
                log.info("Sending request. Retry count: {}", retryCount.get());

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
                    return response;
                } else {
                    retryCount.incrementAndGet();
                }
            } catch (HttpServerErrorException e) {
                handleServerError(e);
                retryCount.incrementAndGet();
            } catch (RestClientException e) {
                log.error("RestClientException. Retrying...", e);
                retryCount.incrementAndGet();
            } catch (Exception e) {
                log.error("Error during JSON serialization:", e);
                retryCount.incrementAndGet();
            }
        }
        // If max retries reached, send to quarantine
        paymentAndLogToQuarantine.sendPaymentToQuarantine(payment);
        throw new RuntimeException("Max retries reached. Unable to get a successful response.");
    }

    private void handleServerError(HttpServerErrorException e) {
        HttpStatus httpStatus = (HttpStatus) e.getStatusCode();
        if (httpStatus.value() == GATEWAY_TIMEOUT) {
            log.warn("Gateway Timeout (504) encountered. Retrying...");
        } else {
            log.error("HTTP Server Error. Retrying... HTTP status code: {}", httpStatus.value(), e);
        }
    }
}