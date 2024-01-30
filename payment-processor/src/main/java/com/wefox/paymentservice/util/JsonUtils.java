package com.wefox.paymentservice.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Payment convertJsonToPayment(String json) {
        try {
            return objectMapper.readValue(json, Payment.class);
        } catch (Exception e) {
            log.error("Error mapping JSON to Payment", e);
            return null;
        }
    }
}
