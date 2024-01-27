package com.wefox.paymentservice.util;

import com.wefox.paymentservice.model.Payment;
import com.wefox.paymentservice.model.PaymentError;

public class PaymentConversionUtil {

    public static PaymentError convertToError(Payment payment) {
        PaymentError paymentError = new PaymentError();
        paymentError.setId(payment.getPaymentId());
        paymentError.setErrorType(payment.getPaymentType());
        paymentError.setErrorDescription("Error occurred during payment processing");
        return paymentError;
    }
}

