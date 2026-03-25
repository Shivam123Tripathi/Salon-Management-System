package com.salon.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.salon.exception.PaymentException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * RAZORPAY PAYMENT SERVICE
 *
 * Integrates with Razorpay payment gateway to handle:
 * - Order creation
 * - Payment verification
 * - Refund processing
 */
@Service
public class RazorpayService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayService.class);

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.enabled:false}")
    private boolean enabled;

    private RazorpayClient client;

    /**
     * CREATE RAZORPAY ORDER
     *
     * Steps:
     * 1. Creates an order on Razorpay with amount and currency
     * 2. Returns order ID to be used in payment UI
     * 3. Customer completes payment on frontend using this order ID
     *
     * @param amount Amount in INR (e.g., 500.00)
     * @param receiptId Unique receipt ID for tracking
     * @return Razorpay order ID
     */
    public String createOrder(BigDecimal amount, String receiptId) {
        if (!enabled) {
            logger.warn("Razorpay is disabled. Returning dummy order ID.");
            return "DUMMY_ORDER_" + System.currentTimeMillis();
        }

        try {
            if (client == null) {
                client = new RazorpayClient(keyId, keySecret);
            }

            JSONObject orderRequest = new JSONObject();
            // Convert amount to paise (1 INR = 100 paise)
            orderRequest.put("amount", amount.multiply(new BigDecimal(100)).intValue());
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);
            orderRequest.put("payment_capture", 1); // Auto capture payment

            Order order = client.orders.create(orderRequest);

            logger.info("Razorpay order created: {} for amount: {}", order.get("id"), amount);
            return order.get("id");

        } catch (RazorpayException e) {
            logger.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new PaymentException("Failed to create payment order: " + e.getMessage());
        }
    }

    /**
     * VERIFY PAYMENT SIGNATURE
     *
     * After payment is completed on frontend, verify the signature to ensure
     * the payment is genuine and not tampered with.
     *
     * @param orderId Razorpay order ID
     * @param paymentId Razorpay payment ID
     * @param signature Razorpay signature to verify
     * @return true if payment is valid, false otherwise
     */
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        if (!enabled) {
            logger.warn("Razorpay is disabled. Skipping signature verification.");
            return true; // Allow for testing without actual payment
        }

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(attributes, keySecret);

        } catch (RazorpayException e) {
            logger.error("Payment verification failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * INITIATE REFUND
     *
     * Processes refund for a payment.
     * Used when appointment is cancelled and payment needs to be returned.
     *
     * @param paymentId Razorpay payment ID
     * @param amount Amount to refund (null for full refund)
     * @return Refund ID
     */
    public String initiateRefund(String paymentId, BigDecimal amount) {
        if (!enabled) {
            logger.warn("Razorpay is disabled. Returning dummy refund ID.");
            return "DUMMY_REFUND_" + System.currentTimeMillis();
        }

        try {
            if (client == null) {
                client = new RazorpayClient(keyId, keySecret);
            }

            JSONObject refundRequest = new JSONObject();
            if (amount != null) {
                refundRequest.put("amount", amount.multiply(new BigDecimal(100)).intValue());
            }

            com.razorpay.Refund refund = client.payments.refund(paymentId, refundRequest);

            logger.info("Refund initiated: {} for payment: {}", refund.get("id"), paymentId);
            return refund.get("id");

        } catch (RazorpayException e) {
            logger.error("Failed to initiate refund: {}", e.getMessage());
            throw new PaymentException("Failed to process refund: " + e.getMessage());
        }
    }
}
