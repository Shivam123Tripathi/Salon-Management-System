package com.salon.entity;

import com.salon.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * APPOINTMENT ENTITY - The core of the booking system.
 * 
 * This represents a single booking: "Customer X booked Artist Y for Service Z
 * on Date D from StartTime to EndTime."
 * 
 * RELATIONSHIPS:
 * - customer (User) → WHO booked
 * - artist (Artist) → WHO will perform the service
 * - service (SalonService) → WHAT service
 * 
 * WHY SEPARATE date, startTime, endTime?
 * - appointmentDate (LocalDate): Just the date (2024-01-15)
 * - startTime (LocalTime): Just the time (10:00)
 * - endTime (LocalTime): Just the time (10:30)
 * Separating them makes slot calculations much easier than using a single DateTime.
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The customer who booked this appointment */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    /** The artist who will perform the service */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    /** The service being booked */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private SalonService service;

    /** Date of the appointment (just the date, no time) */
    @Column(nullable = false)
    private LocalDate appointmentDate;

    /** When the appointment starts (e.g., 10:00) */
    @Column(nullable = false)
    private LocalTime startTime;

    /** When the appointment ends (e.g., 10:30). Auto-calculated from startTime + service.duration */
    @Column(nullable = false)
    private LocalTime endTime;

    /** Current status of this appointment */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    /** Special instructions from the customer (e.g., "I want layers in my haircut") */
    @Column(length = 500)
    private String notes;

    /** When this appointment was created */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** When this appointment was last modified (e.g., rescheduled) */
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
