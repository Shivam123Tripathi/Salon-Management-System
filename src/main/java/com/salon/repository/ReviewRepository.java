package com.salon.repository;

import com.salon.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /** Find all reviews for a specific artist */
    List<Review> findByArtistIdOrderByCreatedAtDesc(Long artistId);

    /** Find all reviews for a specific service */
    List<Review> findByServiceIdOrderByCreatedAtDesc(Long serviceId);

    /** Check if a review already exists for an appointment */
    boolean existsByAppointmentId(Long appointmentId);

    /** Calculate average rating for an artist */
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.artist.id = :artistId")
    Double getAverageRatingByArtistId(@Param("artistId") Long artistId);

    /** Count total reviews for an artist */
    long countByArtistId(Long artistId);
}
