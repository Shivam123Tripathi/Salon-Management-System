package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.dto.ReviewDTO;
import com.salon.dto.ReviewRequest;
import com.salon.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REVIEW CONTROLLER
 * 
 * Handles customer reviews for artists and services.
 * Submitting reviews requires authentication.
 * Viewing reviews is public.
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * SUBMIT A REVIEW
     * 
     * POST /api/reviews
     * Body: { "appointmentId": 5, "rating": 4, "comment": "Great haircut!" }
     * Header: Authorization: Bearer <JWT_TOKEN>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> submitReview(
            Authentication authentication, @Valid @RequestBody ReviewRequest request) {
        ReviewDTO review = reviewService.submitReview(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Review submitted", review));
    }

    /**
     * GET REVIEWS FOR AN ARTIST
     * 
     * GET /api/reviews/artist/1
     */
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getArtistReviews(@PathVariable Long artistId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByArtistId(artistId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", reviews));
    }

    /**
     * GET REVIEWS FOR A SERVICE
     * 
     * GET /api/reviews/service/2
     */
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getServiceReviews(@PathVariable Long serviceId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByServiceId(serviceId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", reviews));
    }
}
