package com.wefox.paymentservice.consumers;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import com.wefox.paymentservice.service.PaymentAndLogToQuarantine;
import com.wefox.paymentservice.service.ProcessorService;
import com.wefox.paymentservice.service.impl.ProcessPaymentOffline;
import com.wefox.paymentservice.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

@Service
public class PaymentOfflineConsumer implements PaymentConsumer{
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOfflineConsumer.class);
    private final ProcessorService processorService;

    @Autowired
    public PaymentOfflineConsumer(PaymentDataRepository dataRepository, RestOperations restOperations, PaymentAndLogToQuarantine paymentAndLogToQuarantine) {
        //STRATEGY Pattern:  Implement the best way to process this kind of payments.
        this.processorService = new ProcessPaymentOffline(dataRepository, restOperations, paymentAndLogToQuarantine);
    }

    @KafkaListener(topics = "offline", groupId = "myGroup")
    public void consume(String eventMessage) {
        LOGGER.info(String.format("Offline--Message message recieved -> %s", eventMessage));
        try {
            Payment payment = JsonUtils.convertJsonToPayment(eventMessage);
            processorService.processPayment(payment);
        } catch (Exception e) {
            LOGGER.error("Error mapping message to Payment", e);
        }
    }
}