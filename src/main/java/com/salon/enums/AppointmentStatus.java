package com.salon.enums;

/**
 * Tracks the lifecycle of an appointment.
 * 
 * Flow: BOOKED → COMPLETED (or CANCELLED or NO_SHOW)
 * 
 * - BOOKED: Customer has confirmed the appointment
 * - COMPLETED: Service was performed successfully
 * - CANCELLED: Customer or admin cancelled before the appointment
 * - NO_SHOW: Customer didn't show up
 */
public enum AppointmentStatus {
    BOOKED,
    COMPLETED,
    CANCELLED,
    NO_SHOW
}
