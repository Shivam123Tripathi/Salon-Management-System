package com.salon.entity;

import com.salon.enums.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

/**
 * ARTIST ENTITY - Represents a salon professional (hair stylist, beautician, etc.)
 * 
 * RELATIONSHIP TO USER:
 * An Artist IS a User with extra fields (specialization, working hours, etc.)
 * We link them using a Foreign Key (user_id) instead of inheritance because:
 * 1. Not all Users are Artists
 * 2. It's simpler to query and manage
 * 3. An Artist always has a corresponding User record for login/authentication
 * 
 * @ManyToOne: Many artists belong to... well, one user each. But the annotation means
 * "this side of the relationship holds the foreign key."
 * 
 * @JoinColumn(name = "user_id"): The actual column name in the "artists" table
 * that stores the foreign key pointing to the "users" table.
 */
@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Links this artist to their User account.
     * @OneToOne because ONE artist corresponds to exactly ONE user.
     * FetchType.LAZY: Don't load the User object until we actually need it.
     * This improves performance — if we only need artist.name, we don't query the users table.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** e.g., "Hair Stylist", "Nail Technician", "Makeup Artist" */
    private String specialization;

    /** Years of professional experience */
    private Integer experienceYears;

    /** 
     * Average rating from customer reviews (1.0 to 5.0).
     * Updated every time a new review is submitted.
     */
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double rating = 0.0;

    /** Total number of reviews received — used to calculate the average rating */
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalReviews = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    /** When this artist's work shift starts (e.g., 09:00) */
    private LocalTime workingHoursStart;

    /** When this artist's work shift ends (e.g., 18:00) */
    private LocalTime workingHoursEnd;

    /** Break start time (e.g., 13:00 for lunch break) */
    private LocalTime breakStartTime;

    /** Break end time (e.g., 14:00) */
    private LocalTime breakEndTime;

    /** Profile image URL */
    @Column(length = 500)
    private String imageUrl;
}
