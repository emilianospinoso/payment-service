package com.wefox.paymentservice.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.paymentservice.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Payment convertJsonToPayment(String json) {
        try {
            return objectMapper.readValue(json, Payment.class);
        } catch (Exception e) {
            LOGGER.error("Error mapping JSON to Payment", e);
            return null;
        }
    }
}
