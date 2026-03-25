package com.salon.dto;

import lombok.*;
import java.time.LocalTime;

/**
 * SLOT DTO
 * 
 * Represents a single time slot for booking.
 * 
 * The available slots are generated dynamically by the SlotService based on:
 * 1. Salon opening/closing times
 * 2. Artist working hours
 * 3. Service duration
 * 4. Existing bookings (to avoid double-booking)
 * 5. Artist breaks and leaves
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotDTO {

    /** Start time of the slot (e.g., "10:00") */
    private LocalTime startTime;

    /** End time of the slot (e.g., "10:30") */
    private LocalTime endTime;

    /** 
     * Status of the slot:
     * - "AVAILABLE": Can be booked
     * - "BOOKED": Already taken by another customer
     * - "BLOCKED": During break time or outside working hours
     */
    private String status;
}
