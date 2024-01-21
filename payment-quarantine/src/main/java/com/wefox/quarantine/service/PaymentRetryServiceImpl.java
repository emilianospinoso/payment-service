package com.wefox.quarantine.service;

import com.wefox.quarantine.model.Payment;
import com.wefox.quarantine.repository.PaymentErrorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Component
public class PaymentRetryServiceImpl implements PaymentRetryService {
    private final PaymentErrorDataRepository paymentRepository;
    private final RestTemplate restOperations;
    private final String paymentEndpoint;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentRetryServiceImpl.class);

    public PaymentRetryServiceImpl(PaymentErrorDataRepository paymentRepository, RestTemplate restOperations, @Value("${payment.endpoint}") String paymentEndpoint) {
        this.paymentRepository = paymentRepository;
        this.restOperations = restOperations;
        this.paymentEndpoint = paymentEndpoint;
    }
    @Scheduled(fixedRate = 2 * 60 * 60 * 1000) // Each 2 hours
    @Async
    public void processPaymentsAgain() {
        LOGGER.info("Processing payments at {}", LocalDateTime.now());

        List<Payment> payments = getAllPayments();

        for (Payment payment : payments) {
            try {
                sendPayment(payment);
            } catch (HttpServerErrorException e) {
                LOGGER.error("Error sending payment {} - {}", payment.getPaymentId(), e.getMessage());
                LOGGER.error("Start alarm in New Relic!!!");
            } catch (Exception e) {
                LOGGER.error("Error processing payment {} - {}", payment.getPaymentId(), e.getMessage());
            }
        }
    }
    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private void sendPayment(Payment payment) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Payment> requestEntity = new HttpEntity<>(payment, headers);

        ResponseEntity<String> response = restOperations.exchange(
                paymentEndpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            LOGGER.info("Payment {} sent successfully", payment.getPaymentId());
        }
    }
}
