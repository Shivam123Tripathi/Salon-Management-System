package com.salon.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ARTIST-SERVICE MAPPING - Join table that tracks which artist can perform which services.
 * 
 * WHY A SEPARATE ENTITY?
 * Artists and Services have a Many-to-Many relationship:
 *   - One artist can perform MANY services (haircut, beard trim, facial)
 *   - One service can be performed by MANY artists
 * 
 * In a relational database, Many-to-Many needs a JOIN TABLE.
 * We could use @ManyToMany annotation, but a separate entity gives us more control
 * (we could add extra columns later, like "price override per artist").
 */
@Entity
@Table(name = "artist_service_mappings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"artist_id", "service_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistServiceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private SalonService service;
}
