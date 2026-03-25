package com.salon.repository;

import com.salon.entity.ArtistServiceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistServiceMappingRepository extends JpaRepository<ArtistServiceMapping, Long> {

    /** Find all services an artist can perform */
    List<ArtistServiceMapping> findByArtistId(Long artistId);

    /** Find all artists who can perform a specific service */
    List<ArtistServiceMapping> findByServiceId(Long serviceId);

    /** Check if a specific artist-service mapping exists */
    boolean existsByArtistIdAndServiceId(Long artistId, Long serviceId);

    /** Remove a specific artist-service mapping */
    void deleteByArtistIdAndServiceId(Long artistId, Long serviceId);
}
