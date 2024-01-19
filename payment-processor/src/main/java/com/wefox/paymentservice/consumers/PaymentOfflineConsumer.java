package com.wefox.paymentservice.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.ProcessorService;
import com.wefox.paymentservice.service.impl.ProcessPaymentOffline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;


@Service
public class PaymentOfflineConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOnlineConsumer.class);
    private final ProcessorService processorService;

    @Autowired
    public PaymentOfflineConsumer(PaymentDataRepository dataRepository, RestOperations restOperations) {
        //STRATEGY Pattern:  Implement the best way to process this kind of payments.
        this.processorService = new ProcessPaymentOffline(dataRepository);
    }

    @KafkaListener(topics = "offline", groupId = "myGroup")
    public void consumeOffline(String eventMessage) {
        LOGGER.info(String.format("Offline--Message message recieved -> %s", eventMessage));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Payment payment = objectMapper.readValue(eventMessage, Payment.class);
            // Use the selected processor implementation
            processorService.processPayment(payment);
        } catch (Exception e) {
            // Handle exceptions, e.g., if the message format is invalid
            LOGGER.info("Error mapping message to Payment", e);
        }


    }
}