package com.salon.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * SERVICE DTO
 * 
 * Represents a salon service for the API response.
 * Maps from the SalonService entity but only exposes what the client needs.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDTO {
    private Long id;
    private String name;
    private String description;
    private Integer durationMinutes;
    private BigDecimal price;
    private String category;
    private Boolean active;
}
