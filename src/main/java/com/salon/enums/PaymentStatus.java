package com.salon.enums;

/**
 * Tracks the state of a payment transaction.
 * 
 * - PENDING: Payment initiated but not confirmed yet
 * - COMPLETED: Payment received successfully
 * - REFUNDED: Payment returned to customer (e.g., after cancellation)
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    REFUNDED
}
