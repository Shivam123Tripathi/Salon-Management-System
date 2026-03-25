package com.salon.repository;

import com.salon.entity.Payment;
import com.salon.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /** Find payment for an appointment */
    Optional<Payment> findByAppointmentId(Long appointmentId);

    /** Calculate total revenue from completed payments */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    /** Calculate revenue for a specific date */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' AND CAST(p.paidAt AS LocalDate) = :date")
    BigDecimal getRevenueByDate(@Param("date") LocalDate date);
}
