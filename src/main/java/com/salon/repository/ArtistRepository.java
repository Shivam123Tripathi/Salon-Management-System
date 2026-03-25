package com.salon.repository;

import com.salon.entity.Artist;
import com.salon.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    /** Find all artists with a specific availability status */
    List<Artist> findByAvailabilityStatus(AvailabilityStatus status);

    /** Find artists by specialization (e.g., "Hair Stylist") */
    List<Artist> findBySpecializationContainingIgnoreCase(String specialization);

    /** Find an artist by their user account ID */
    Optional<Artist> findByUserId(Long userId);

    /** Count artists that are currently available */
    long countByAvailabilityStatus(AvailabilityStatus status);
}
