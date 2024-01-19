package com.wefox.paymentservice.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.repository.PaymentDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentOfflineConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOnlineConsumer.class);
    private PaymentDataRepository dataRepository;

    public PaymentOfflineConsumer(PaymentDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @KafkaListener(topics = "offline", groupId = "myGroup")
    public void consumeOffline(String eventMessage) {
        LOGGER.info(String.format("Offline--Message message recieved -> %s", eventMessage));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Payment payment = objectMapper.readValue(eventMessage, Payment.class);

            // Now you can use the 'payment' object as needed
            System.out.println("Mapped Payment -> %s" + payment.toString());
            System.out.println("The Payment id is: " + payment.getPaymentId());
            System.out.println("The Account id is: " + payment.getAccountId());
            System.out.println("The Payment type is: " + payment.getPaymentType());
            System.out.println("The Credit card is: " + payment.getCreditCard());
            System.out.println("The Amount is: " + payment.getAmount());
            System.out.println("The Delay is: " + payment.getDelay());
            dataRepository.save(payment);
        } catch (Exception e) {
            // Handle exceptions, e.g., if the message format is invalid
            LOGGER.error("Error mapping message to Payment", e);
        }
    }

}
