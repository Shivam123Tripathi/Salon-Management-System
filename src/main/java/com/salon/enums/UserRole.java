package com.salon.enums;

/**
 * USER ROLES in the system.
 * 
 * Why an enum? Because roles are a FIXED set of values that never change at runtime.
 * Using an enum instead of a String prevents typos (e.g., "ADMN" instead of "ADMIN")
 * and gives us compile-time safety.
 * 
 * - CUSTOMER: Books appointments, leaves reviews
 * - ADMIN: Manages the salon (artists, services, views reports)
 * - ARTIST: The person who performs the service (optional role)
 */
public enum UserRole {
    CUSTOMER,
    ADMIN,
    ARTIST
}
