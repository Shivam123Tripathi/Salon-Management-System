package com.salon.repository;

import com.salon.entity.SalonConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SALON CONFIG REPOSITORY
 * 
 * Since there's only one row in this table, we just use findAll().get(0)
 * or the inherited findById(1L) to get the salon configuration.
 */
@Repository
public interface SalonConfigRepository extends JpaRepository<SalonConfig, Long> {
}
