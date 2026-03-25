package com.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * REVIEW ENTITY - Customer reviews for artists and services.
 * 
 * A review is tied to a specific APPOINTMENT. This ensures:
 * 1. Only customers who actually visited can leave a review
 * 2. One review per appointment (not multiple reviews for the same visit)
 * 3. We know exactly which service and artist the review is about
 */
@Entity
@Table(name = "reviews",
       uniqueConstraints = @UniqueConstraint(columnNames = {"appointment_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The customer who wrote this review */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    /** The artist being reviewed */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    /** The service being reviewed */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private SalonService service;

    /** The specific appointment this review is for */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    /** Star rating: 1 (terrible) to 5 (excellent) */
    @Column(nullable = false)
    private Integer rating;

    /** Optional text comment */
    @Column(length = 1000)
    private String comment;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
