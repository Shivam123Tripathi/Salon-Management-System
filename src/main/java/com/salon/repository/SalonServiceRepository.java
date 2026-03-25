package com.salon.repository;

import com.salon.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {

    /** Find all active services (not soft-deleted) */
    List<SalonService> findByActiveTrue();

    /** Find active services by category */
    List<SalonService> findByCategoryAndActiveTrue(String category);

    /** Check if a service with this name already exists */
    boolean existsByNameIgnoreCase(String name);
}
