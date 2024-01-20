package com.wefox.quarantine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaymentValidatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentValidatorApplication.class, args);
    }

}

