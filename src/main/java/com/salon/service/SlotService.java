package com.salon.service;

import com.salon.dto.SlotDTO;
import com.salon.entity.Appointment;
import com.salon.entity.Artist;
import com.salon.entity.SalonConfig;
import com.salon.entity.SalonService;
import com.salon.enums.AppointmentStatus;
import com.salon.exception.BadRequestException;
import com.salon.exception.ResourceNotFoundException;
import com.salon.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SLOT SERVICE — THE CORE SCHEDULING ENGINE
 * 
 * This is where the magic happens. It generates available time slots for booking.
 * 
 * ALGORITHM:
 * 1. Determine the effective start/end time (intersection of salon hours & artist hours)
 * 2. Check if artist is on leave → return empty list
 * 3. Generate all possible slots based on service duration
 * 4. For each slot, check:
 *    a. Does it overlap with the artist's break? → BLOCKED
 *    b. Does it overlap with an existing booking? → BOOKED
 *    c. Otherwise → AVAILABLE
 * 5. Return the list of slots with their status
 * 
 * EXAMPLE:
 * Salon: 09:00 - 21:00
 * Artist: 10:00 - 18:00
 * Service duration: 30 min
 * Break: 13:00 - 14:00
 * Existing booking: 11:00 - 11:30
 * 
 * Generated slots:
 * 10:00-10:30 → AVAILABLE
 * 10:30-11:00 → AVAILABLE
 * 11:00-11:30 → BOOKED
 * 11:30-12:00 → AVAILABLE
 * 12:00-12:30 → AVAILABLE
 * 12:30-13:00 → AVAILABLE
 * 13:00-13:30 → BLOCKED (break)
 * 13:30-14:00 → BLOCKED (break)
 * 14:00-14:30 → AVAILABLE
 * ...
 * 17:30-18:00 → AVAILABLE
 */
@Service
public class SlotService {

    private final ArtistRepository artistRepository;
    private final SalonServiceRepository salonServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final SalonConfigRepository salonConfigRepository;
    private final ArtistLeaveRepository artistLeaveRepository;

    public SlotService(ArtistRepository artistRepository,
                       SalonServiceRepository salonServiceRepository,
                       AppointmentRepository appointmentRepository,
                       SalonConfigRepository salonConfigRepository,
                       ArtistLeaveRepository artistLeaveRepository) {
        this.artistRepository = artistRepository;
        this.salonServiceRepository = salonServiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.salonConfigRepository = salonConfigRepository;
        this.artistLeaveRepository = artistLeaveRepository;
    }

    /**
     * Generate available slots for a specific artist, service, and date.
     */
    public List<SlotDTO> getAvailableSlots(Long artistId, Long serviceId, LocalDate date) {
        // Validate inputs
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        SalonService service = salonServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        // Check if date is in the past
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book appointments in the past");
        }

        // Check if artist is on leave
        if (artistLeaveRepository.existsByArtistIdAndLeaveDate(artistId, date)) {
            return List.of(); // Return empty — artist is not available
        }

        // Get salon configuration
        SalonConfig config = salonConfigRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salon configuration not found. Please set up salon config first."));

        // Determine effective working window
        // Take the LATER start time and EARLIER end time between salon and artist
        LocalTime effectiveStart = laterOf(config.getOpeningTime(), artist.getWorkingHoursStart());
        LocalTime effectiveEnd = earlierOf(config.getClosingTime(), artist.getWorkingHoursEnd());

        if (effectiveStart.isAfter(effectiveEnd) || effectiveStart.equals(effectiveEnd)) {
            return List.of(); // No valid working window
        }

        // Get existing bookings for this artist on this date (excluding cancelled)
        List<Appointment> existingBookings = appointmentRepository
                .findByArtistIdAndAppointmentDateAndStatusNot(artistId, date, AppointmentStatus.CANCELLED);

        int durationMinutes = service.getDurationMinutes();
        List<SlotDTO> slots = new ArrayList<>();

        // Generate slots by iterating from start to end in increments of service duration
        LocalTime currentStart = effectiveStart;
        while (currentStart.plusMinutes(durationMinutes).compareTo(effectiveEnd) <= 0) {
            LocalTime currentEnd = currentStart.plusMinutes(durationMinutes);

            String status;

            // Check if slot overlaps with break
            if (isOverlappingWithBreak(currentStart, currentEnd, artist)) {
                status = "BLOCKED";
            }
            // Check if slot overlaps with existing booking
            else if (isOverlappingWithBooking(currentStart, currentEnd, existingBookings)) {
                status = "BOOKED";
            }
            // If today, check if slot is in the past
            else if (date.equals(LocalDate.now()) && currentStart.isBefore(LocalTime.now())) {
                status = "BLOCKED";
            }
            else {
                status = "AVAILABLE";
            }

            slots.add(SlotDTO.builder()
                    .startTime(currentStart)
                    .endTime(currentEnd)
                    .status(status)
                    .build());

            // Move to next slot
            currentStart = currentEnd;
        }

        return slots;
    }

    /** Check if a time range overlaps with the artist's break */
    private boolean isOverlappingWithBreak(LocalTime start, LocalTime end, Artist artist) {
        if (artist.getBreakStartTime() == null || artist.getBreakEndTime() == null) {
            return false; // No break configured
        }
        // Two ranges overlap if: start1 < end2 AND end1 > start2
        return start.isBefore(artist.getBreakEndTime()) && end.isAfter(artist.getBreakStartTime());
    }

    /** Check if a time range overlaps with any existing booking */
    private boolean isOverlappingWithBooking(LocalTime start, LocalTime end,
                                              List<Appointment> existingBookings) {
        for (Appointment booking : existingBookings) {
            if (start.isBefore(booking.getEndTime()) && end.isAfter(booking.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    /** Return the later of two times */
    private LocalTime laterOf(LocalTime a, LocalTime b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }

    /** Return the earlier of two times */
    private LocalTime earlierOf(LocalTime a, LocalTime b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isBefore(b) ? a : b;
    }
}
