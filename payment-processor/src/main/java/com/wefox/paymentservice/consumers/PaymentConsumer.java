package com.wefox.paymentservice.consumers;

public interface PaymentConsumer {
    void consume(String eventMessage);
}
