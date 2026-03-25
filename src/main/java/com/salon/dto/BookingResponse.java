package com.salon.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * BOOKING RESPONSE DTO
 * 
 * Returned after a successful booking, or when listing appointments.
 * Contains all the details the customer needs to see about their appointment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private String customerName;
    private Long artistId;
    private String artistName;
    private Long serviceId;
    private String serviceName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String notes;
    private BigDecimal price;
    private String paymentStatus;
    private String paymentMethod;
}
