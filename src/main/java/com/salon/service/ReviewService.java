package com.salon.service;

import com.salon.dto.ReviewDTO;
import com.salon.dto.ReviewRequest;
import com.salon.entity.*;
import com.salon.enums.AppointmentStatus;
import com.salon.exception.BadRequestException;
import com.salon.exception.ResourceNotFoundException;
import com.salon.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REVIEW SERVICE
 * 
 * Handles customer reviews for artists and services.
 * 
 * BUSINESS RULES:
 * 1. Can only review COMPLETED appointments
 * 2. One review per appointment (no duplicate reviews)
 * 3. When a new review is submitted, the artist's average rating is recalculated
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         AppointmentRepository appointmentRepository,
                         ArtistRepository artistRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
    }

    /**
     * Submit a review for a completed appointment.
     * Also updates the artist's average rating.
     */
    @Transactional
    public ReviewDTO submitReview(String customerEmail, ReviewRequest request) {
        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Verify the customer owns this appointment
        if (!appointment.getCustomer().getEmail().equals(customerEmail)) {
            throw new BadRequestException("You can only review your own appointments");
        }

        // Check appointment is completed
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new BadRequestException("You can only review completed appointments");
        }

        // Check for duplicate review
        if (reviewRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new BadRequestException("You have already reviewed this appointment");
        }

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Create the review
        Review review = Review.builder()
                .customer(customer)
                .artist(appointment.getArtist())
                .service(appointment.getService())
                .appointment(appointment)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        // Update artist's average rating
        updateArtistRating(appointment.getArtist().getId());

        return mapToDTO(saved);
    }

    /** Get all reviews for a specific artist */
    public List<ReviewDTO> getReviewsByArtistId(Long artistId) {
        return reviewRepository.findByArtistIdOrderByCreatedAtDesc(artistId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /** Get all reviews for a specific service */
    public List<ReviewDTO> getReviewsByServiceId(Long serviceId) {
        return reviewRepository.findByServiceIdOrderByCreatedAtDesc(serviceId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recalculate and update the artist's average rating.
     * Called every time a new review is submitted.
     */
    private void updateArtistRating(Long artistId) {
        Double avgRating = reviewRepository.getAverageRatingByArtistId(artistId);
        long totalReviews = reviewRepository.countByArtistId(artistId);

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        artist.setRating(avgRating);
        artist.setTotalReviews((int) totalReviews);
        artistRepository.save(artist);
    }

    /** Helper: Convert Review entity → ReviewDTO */
    private ReviewDTO mapToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .customerName(review.getCustomer().getFullName())
                .artistName(review.getArtist().getUser().getFullName())
                .serviceName(review.getService().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt() != null ? review.getCreatedAt().toString() : null)
                .build();
    }
}
