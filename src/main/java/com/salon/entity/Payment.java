package com.salon.entity;

import com.salon.enums.PaymentMethod;
import com.salon.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PAYMENT ENTITY - Records payment information for an appointment.
 * 
 * Note: This stores the RECORD of payment, not the actual payment processing.
 * Actual UPI/Card processing requires integration with a payment gateway
 * (Razorpay, Stripe, etc.) which is outside the scope of this backend.
 * 
 * PAY_AT_SALON: The simplest flow — customer pays cash at the salon.
 * The salon staff marks it as COMPLETED after receiving payment.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Which appointment this payment is for. One appointment = one payment. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    /** Payment amount (should match the service price) */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /** How the customer paid */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    /** Current payment status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    /** 
     * External transaction ID from payment gateway (e.g., Razorpay order ID).
     * Null for PAY_AT_SALON payments.
     */
    private String transactionId;

    /** When the payment was completed */
    private LocalDateTime paidAt;
}
