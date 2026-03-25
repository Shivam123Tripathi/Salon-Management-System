package com.salon.repository;

import com.salon.entity.Appointment;
import com.salon.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * APPOINTMENT REPOSITORY
 * 
 * This has the most complex queries because appointments involve:
 * - Double-booking prevention
 * - Filtering by date, artist, service, status
 * - Dashboard analytics
 * 
 * @Query: When Spring can't generate the SQL from the method name alone,
 * we write JPQL (Java Persistence Query Language) — it's like SQL but uses
 * entity class names instead of table names.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /** Find all appointments for a specific customer */
    List<Appointment> findByCustomerIdOrderByAppointmentDateDescStartTimeDesc(Long customerId);

    /** Find appointments for a customer with a specific status */
    List<Appointment> findByCustomerIdAndStatus(Long customerId, AppointmentStatus status);

    /** Find all appointments for an artist on a specific date (for slot generation) */
    List<Appointment> findByArtistIdAndAppointmentDateAndStatusNot(
            Long artistId, LocalDate date, AppointmentStatus excludeStatus);

    /**
     * DOUBLE-BOOKING CHECK
     * 
     * Checks if an artist already has an appointment that OVERLAPS with the requested time slot.
     * 
     * Two time ranges overlap if: existingStart < newEnd AND existingEnd > newStart
     * 
     * Example:
     *   Existing: 10:00-10:30
     *   Requested: 10:15-10:45 → OVERLAPS (10:00 < 10:45 AND 10:30 > 10:15)
     *   Requested: 10:30-11:00 → NO OVERLAP (10:00 < 11:00 BUT 10:30 is NOT > 10:30)
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.artist.id = :artistId " +
           "AND a.appointmentDate = :date " +
           "AND a.status <> 'CANCELLED' " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean hasOverlappingAppointment(
            @Param("artistId") Long artistId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    /** Count appointments by status (for dashboard) */
    long countByStatus(AppointmentStatus status);

    /** Count bookings on a specific date (for dashboard - today's count) */
    long countByAppointmentDate(LocalDate date);

    /** Find appointments on a specific date (for admin filtering) */
    List<Appointment> findByAppointmentDate(LocalDate date);

    /** Find appointments by artist (for admin filtering) */
    List<Appointment> findByArtistId(Long artistId);

    /** Find recent bookings ordered by creation (for dashboard) */
    List<Appointment> findTop10ByOrderByCreatedAtDesc();

    /** Find appointments between two dates (for reports) */
    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);

    /** Find appointments by date range and status */
    List<Appointment> findByAppointmentDateBetweenAndStatus(
            LocalDate startDate, LocalDate endDate, AppointmentStatus status);
}
