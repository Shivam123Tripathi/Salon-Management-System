package com.salon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * BOOKING REQUEST DTO
 * 
 * Sent by the Android app to book an appointment:
 * POST /api/appointments
 * Body: {
 *   "artistId": 1,
 *   "serviceId": 2,
 *   "appointmentDate": "2024-01-15",
 *   "startTime": "10:00",
 *   "notes": "I want layers"
 * }
 * 
 * The endTime is NOT sent by the client — we calculate it automatically:
 * endTime = startTime + service.durationMinutes
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Artist ID is required")
    private Long artistId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    private String notes;

    /** Payment method: "UPI", "CARD", or "PAY_AT_SALON" */
    private String paymentMethod;
}
