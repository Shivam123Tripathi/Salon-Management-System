package com.salon.enums;

/**
 * How the customer pays for the service.
 * 
 * - UPI: Unified Payments Interface (Google Pay, PhonePe, etc.)
 * - CARD: Credit/Debit card
 * - PAY_AT_SALON: Cash payment at the salon (no online gateway needed)
 */
public enum PaymentMethod {
    UPI,
    CARD,
    PAY_AT_SALON
}
