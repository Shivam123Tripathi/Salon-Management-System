package com.salon.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * PAYMENT DTO
 * 
 * Used for recording and viewing payment information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private Long appointmentId;
    private BigDecimal amount;
    private String method;
    private String status;
    private String transactionId;
}
