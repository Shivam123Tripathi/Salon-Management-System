package com.salon.exception;

/**
 * PAYMENT EXCEPTION
 *
 * Thrown when payment processing fails.
 */
public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
