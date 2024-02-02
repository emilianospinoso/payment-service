package com.wefox.paymentservice.consumers;

import com.wefox.paymentservice.exceptions.PaymentProcessingException;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.ProcessorService;
import com.wefox.paymentservice.service.impl.ProcessPaymentsOnline;
import com.wefox.paymentservice.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

@Slf4j
@Service
public class PaymentOnlineConsumer implements PaymentConsumer {
    private final ProcessorService processorService;

    @Autowired
    public PaymentOnlineConsumer(PaymentDataRepository dataRepository, RestOperations restOperations, PaymentAndLogToQuarantine paymentAndLogToQuarantine) {
        // Strategy Pattern to implement the best strategy to process the payments
        this.processorService = new ProcessPaymentsOnline(dataRepository, restOperations, paymentAndLogToQuarantine);
    }

    @KafkaListener(topics = "online", groupId = "myGroup", concurrency = "5")
    public void consume(String eventMessage) {
        try {
            log.info(String.format("Online--Message message recieved -> %s", eventMessage));
            Payment payment = JsonUtils.convertJsonToPayment(eventMessage);
            // Use the selected processor implementation
            processorService.processPayment(payment);
        } catch (Exception e) {
            log.error("Error processing payment: ", e);
            throw new PaymentProcessingException("Error processing payment", e);
        }
    }
}
