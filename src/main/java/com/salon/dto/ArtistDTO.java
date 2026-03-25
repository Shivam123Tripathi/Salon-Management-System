package com.salon.dto;

import lombok.*;
import java.util.List;

/**
 * ARTIST DTO
 * 
 * Represents an artist in API responses.
 * Includes their user info, specialization, rating, and the services they can perform.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String specialization;
    private Integer experienceYears;
    private Double rating;
    private Integer totalReviews;
    private String availabilityStatus;
    private String workingHoursStart;
    private String workingHoursEnd;
    private List<String> serviceNames;
}
