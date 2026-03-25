package com.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

/**
 * SALON CONFIG ENTITY - Stores salon-wide settings.
 * 
 * This is a SINGLETON table — there should only be ONE row in this table.
 * It holds global configuration like opening/closing times and cancellation policies.
 * 
 * WHY A DATABASE TABLE AND NOT application.properties?
 * Because the admin should be able to change these settings via the Admin panel
 * WITHOUT restarting the server. If we put them in application.properties,
 * changes would require a server restart.
 */
@Entity
@Table(name = "salon_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Salon name for display purposes */
    @Column(nullable = false)
    private String salonName;

    /** When the salon opens (e.g., 09:00) */
    @Column(nullable = false)
    private LocalTime openingTime;

    /** When the salon closes (e.g., 21:00) */
    @Column(nullable = false)
    private LocalTime closingTime;

    /**
     * How many hours before the appointment a customer can cancel.
     * e.g., 2 means customer must cancel at least 2 hours before the slot.
     * Cancellations after this window may incur a penalty.
     */
    @Column(nullable = false)
    private Integer cancellationPolicyHours = 2;

    /**
     * Default slot interval in minutes.
     * Used when generating slots if the service doesn't have its own duration.
     */
    @Column(nullable = false)
    private Integer defaultSlotIntervalMinutes = 30;
}
