package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.dto.PaymentDTO;
import com.salon.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * PAYMENT CONTROLLER
 *
 * Handles payment operations including:
 * - Creating Razorpay orders
 * - Verifying payments
 * - Processing refunds
 * Requires authentication.
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * GET PAYMENT FOR AN APPOINTMENT
     *
     * GET /api/payments/appointment/5
     */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPayment(@PathVariable Long appointmentId) {
        PaymentDTO payment = paymentService.getPaymentByAppointmentId(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved", payment));
    }

    /**
     * CREATE RAZORPAY ORDER
     *
     * POST /api/payments/create-order
     * Body: { "appointmentId": 5, "amount": 500 }
     *
     * Returns Razorpay order ID to be used in frontend payment UI
     */
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<Map<String, String>>> createOrder(
            @RequestBody Map<String, Object> request) {

        Long appointmentId = Long.valueOf(request.get("appointmentId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        String orderId = paymentService.createOnlinePaymentOrder(appointmentId, amount);

        return ResponseEntity.ok(ApiResponse.success("Order created",
                Map.of("orderId", orderId)));
    }

    /**
     * VERIFY RAZORPAY PAYMENT
     *
     * POST /api/payments/verify
     * Body: {
     *   "appointmentId": 5,
     *   "razorpayOrderId": "order_xxx",
     *   "razorpayPaymentId": "pay_xxx",
     *   "razorpaySignature": "signature_xxx"
     * }
     *
     * Verifies the payment signature and marks payment as completed
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentDTO>> verifyPayment(
            @RequestBody Map<String, Object> request) {

        Long appointmentId = Long.valueOf(request.get("appointmentId").toString());
        String razorpayOrderId = request.get("razorpayOrderId").toString();
        String razorpayPaymentId = request.get("razorpayPaymentId").toString();
        String razorpaySignature = request.get("razorpaySignature").toString();

        PaymentDTO payment = paymentService.verifyAndCompletePayment(
                appointmentId, razorpayOrderId, razorpayPaymentId, razorpaySignature);

        return ResponseEntity.ok(ApiResponse.success("Payment verified and completed", payment));
    }

    /**
     * MARK PAYMENT AS COMPLETED
     *
     * PUT /api/payments/appointment/5/complete?transactionId=TXN123
     * Used when payment is received at salon or confirmed from gateway.
     */
    @PutMapping("/appointment/{appointmentId}/complete")
    public ResponseEntity<ApiResponse<PaymentDTO>> completePayment(
            @PathVariable Long appointmentId,
            @RequestParam(required = false) String transactionId) {
        PaymentDTO payment = paymentService.completePayment(appointmentId, transactionId);
        return ResponseEntity.ok(ApiResponse.success("Payment completed", payment));
    }

    /**
     * PROCESS REFUND
     *
     * POST /api/payments/appointment/5/refund
     * Processes refund for cancelled appointment
     */
    @PostMapping("/appointment/{appointmentId}/refund")
    public ResponseEntity<ApiResponse<PaymentDTO>> refundPayment(@PathVariable Long appointmentId) {
        PaymentDTO payment = paymentService.processRefund(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Refund processed successfully", payment));
    }
}
