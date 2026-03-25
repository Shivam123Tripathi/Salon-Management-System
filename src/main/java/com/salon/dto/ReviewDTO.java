package com.salon.dto;

import lombok.*;

/**
 * REVIEW DTO
 * 
 * Returned when listing reviews for an artist or service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private String customerName;
    private String artistName;
    private String serviceName;
    private Integer rating;
    private String comment;
    private String createdAt;
}
