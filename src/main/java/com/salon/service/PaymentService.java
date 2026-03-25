package com.salon.service;

import com.salon.dto.PaymentDTO;
import com.salon.entity.Payment;
import com.salon.enums.PaymentMethod;
import com.salon.enums.PaymentStatus;
import com.salon.exception.ResourceNotFoundException;
import com.salon.exception.PaymentException;
import com.salon.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PAYMENT SERVICE
 *
 * Handles payment recording, gateway integration, and status updates.
 * Integrates with Razorpay for online payments.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;

    public PaymentService(PaymentRepository paymentRepository, RazorpayService razorpayService) {
        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
    }

    /** Get payment details for an appointment */
    public PaymentDTO getPaymentByAppointmentId(Long appointmentId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for appointment ID: " + appointmentId));
        return mapToDTO(payment);
    }

    /** Create Razorpay order for online payment */
    public String createOnlinePaymentOrder(Long appointmentId, BigDecimal amount) {
        // Create Razorpay order
        String receiptId = "APT_" + appointmentId + "_" + System.currentTimeMillis();
        return razorpayService.createOrder(amount, receiptId);
    }

    /** Verify and complete online payment */
    public PaymentDTO verifyAndCompletePayment(
            Long appointmentId,
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature) {

        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for appointment ID: " + appointmentId));

        // Verify signature
        boolean isValid = razorpayService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);
        if (!isValid) {
            throw new PaymentException("Payment verification failed. Invalid signature.");
        }

        // Update payment
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(razorpayPaymentId);
        payment.setPaidAt(LocalDateTime.now());

        return mapToDTO(paymentRepository.save(payment));
    }

    /** Mark a payment as completed (e.g., after receiving payment at salon) */
    public PaymentDTO completePayment(Long appointmentId, String transactionId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for appointment ID: " + appointmentId));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setPaidAt(LocalDateTime.now());

        return mapToDTO(paymentRepository.save(payment));
    }

    /** Process refund for cancelled appointment */
    public PaymentDTO processRefund(Long appointmentId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for appointment ID: " + appointmentId));

        // Only refund if payment was completed and paid online
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Cannot refund: Payment not completed");
        }

        if (payment.getMethod() == PaymentMethod.PAY_AT_SALON) {
            throw new PaymentException("Cannot refund: Payment was made at salon");
        }

        // Initiate refund with Razorpay
        String refundId = razorpayService.initiateRefund(payment.getTransactionId(), payment.getAmount());

        // Update payment status
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setTransactionId(payment.getTransactionId() + " | REFUND: " + refundId);

        return mapToDTO(paymentRepository.save(payment));
    }

    /** Helper: Convert Payment entity → PaymentDTO */
    private PaymentDTO mapToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .appointmentId(payment.getAppointment().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod().name())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .build();
    }
}
