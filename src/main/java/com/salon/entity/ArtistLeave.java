package com.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * ARTIST LEAVE ENTITY - Tracks days when an artist is not available.
 * 
 * When generating available slots, we check this table.
 * If an artist has a leave record for the requested date, NO slots are generated.
 * 
 * This is separate from the AvailabilityStatus because:
 * - AvailabilityStatus is the CURRENT real-time status (Available, Busy)
 * - ArtistLeave is PLANNED future absences (sick leave, vacation, etc.)
 */
@Entity
@Table(name = "artist_leaves",
       uniqueConstraints = @UniqueConstraint(columnNames = {"artist_id", "leave_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    /** The date of the leave */
    @Column(name = "leave_date", nullable = false)
    private LocalDate leaveDate;

    /** Reason for leave (optional) */
    private String reason;
}
