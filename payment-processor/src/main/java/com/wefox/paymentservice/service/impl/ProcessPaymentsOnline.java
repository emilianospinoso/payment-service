package com.wefox.paymentservice.service.impl;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.LogsToQuarantine;
import com.wefox.paymentservice.service.ProcessorService;
import com.wefox.paymentservice.service.StoreLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessPaymentsOnline implements ProcessorService {
    private final PaymentDataRepository dataRepository;
    private final RestOperations restOperations;

    private final StoreLogsService storeLogsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPaymentsOnline.class);

    // Adjust maxRetries according to your needs
    private static final int MAX_RETRIES = 3;

    public ProcessPaymentsOnline(PaymentDataRepository dataRepository, RestOperations restOperations) {
        this.dataRepository = dataRepository;
        this.restOperations = restOperations;
        this.storeLogsService = new SaveLogsIntoSystem(restOperations, new LogsToQuarantine(restOperations));
    }

    @Override
    public void processPayment(Payment payment) {
        try {
            ResponseEntity<String> response = sendRequestWithRetry(payment);

            if (response.getStatusCode().is2xxSuccessful()&&payment.getAccountId()==3) {
                dataRepository.save(payment);
            } else {
                storeLogsService.sendLogsToDefaultStorage(payment);
            }
        } catch (Exception e) {
            LOGGER.error("---Error trying to process Payment Online- :" + e);
        }
    }

    private ResponseEntity<String> sendRequestWithRetry(Payment payment) {
        AtomicInteger retryCount = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            return CompletableFuture.supplyAsync(() -> {
                        LOGGER.info("Sending request. Retry count: {}", retryCount.get());
                        return restOperations.postForEntity("http://localhost:9000/payment", payment, String.class);
                    }, executorService)
                    .exceptionally(e -> {
                        LOGGER.error("Error trying to process Payment Online- :", e);
                        throw new RuntimeException("Max retries reached. Unable to get a successful response.", e);
                    })
                    .join();
        } finally {
            executorService.shutdown();
        }
    }
}
