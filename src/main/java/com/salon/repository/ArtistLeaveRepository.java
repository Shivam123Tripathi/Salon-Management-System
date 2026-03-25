package com.salon.repository;

import com.salon.entity.ArtistLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArtistLeaveRepository extends JpaRepository<ArtistLeave, Long> {

    /** Check if an artist is on leave on a specific date */
    boolean existsByArtistIdAndLeaveDate(Long artistId, LocalDate leaveDate);

    /** Find all leaves for an artist */
    List<ArtistLeave> findByArtistIdOrderByLeaveDateDesc(Long artistId);

    /** Find all leaves for an artist within a date range */
    List<ArtistLeave> findByArtistIdAndLeaveDateBetween(Long artistId, LocalDate startDate, LocalDate endDate);
}
