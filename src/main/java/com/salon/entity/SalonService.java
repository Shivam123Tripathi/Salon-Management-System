package com.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * SALON SERVICE ENTITY - Represents a service the salon offers (e.g., Haircut, Facial).
 * 
 * WHY "SalonService" AND NOT "Service"?
 * "Service" is a very common word in Spring (Spring has its own @Service annotation).
 * Naming it "SalonService" avoids confusion. The TABLE is still named "services" in MySQL.
 * 
 * WHY BigDecimal FOR PRICE?
 * Never use double/float for money! They have floating-point precision issues:
 *   0.1 + 0.2 = 0.30000000000000004 (with double)
 * BigDecimal handles money accurately: 0.1 + 0.2 = 0.3
 */
@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name of the service: "Haircut", "Beard Trim", "Facial", etc. */
    @Column(nullable = false)
    private String name;

    /** Detailed description of what the service includes */
    @Column(length = 1000)
    private String description;

    /** How long the service takes in minutes (e.g., 30 for a haircut) */
    @Column(nullable = false)
    private Integer durationMinutes;

    /** Cost of the service. Using BigDecimal for precise money handling. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Category for grouping: "Hair", "Skin", "Nails", "Makeup", etc. */
    private String category;

    /**
     * Soft delete flag. Instead of actually deleting a service from the database
     * (which would break references in past appointments), we set active = false.
     * The service won't show up in listings but historical data is preserved.
     */
    @Column(nullable = false)
    private Boolean active = true;

    /** Service image URL */
    @Column(length = 500)
    private String imageUrl;
}
