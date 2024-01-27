package com.wefox.quarantine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaymentQuarantineApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentQuarantineApplication.class, args);
    }

}

