package com.salon.service;

import com.salon.dto.BookingResponse;
import com.salon.dto.DashboardDTO;
import com.salon.entity.Appointment;
import com.salon.entity.Payment;
import com.salon.enums.AppointmentStatus;
import com.salon.enums.AvailabilityStatus;
import com.salon.enums.PaymentStatus;
import com.salon.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DASHBOARD SERVICE
 * 
 * Provides aggregated stats for the admin dashboard:
 * - Total/today's bookings
 * - Revenue figures
 * - Active artist count
 * - Recent bookings list
 */
@Service
public class DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final ArtistRepository artistRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(AppointmentRepository appointmentRepository,
                            ArtistRepository artistRepository,
                            PaymentRepository paymentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.artistRepository = artistRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardDTO getDashboardStats() {
        long totalBookings = appointmentRepository.count();
        long todayBookings = appointmentRepository.countByAppointmentDate(LocalDate.now());
        long activeArtists = artistRepository.countByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        long completedBookings = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long cancelledBookings = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);

        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();
        BigDecimal todayRevenue = paymentRepository.getRevenueByDate(LocalDate.now());

        // Get 10 most recent bookings
        List<BookingResponse> recentBookings = appointmentRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalBookings(totalBookings)
                .todayBookings(todayBookings)
                .activeArtists(activeArtists)
                .totalRevenue(totalRevenue)
                .todayRevenue(todayRevenue)
                .completedBookings(completedBookings)
                .cancelledBookings(cancelledBookings)
                .recentBookings(recentBookings)
                .build();
    }

    private BookingResponse mapToBookingResponse(Appointment appointment) {
        Payment payment = paymentRepository.findByAppointmentId(appointment.getId()).orElse(null);

        return BookingResponse.builder()
                .id(appointment.getId())
                .customerName(appointment.getCustomer().getFullName())
                .artistName(appointment.getArtist().getUser().getFullName())
                .serviceName(appointment.getService().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus().name())
                .price(appointment.getService().getPrice())
                .paymentStatus(payment != null ? payment.getStatus().name() : null)
                .paymentMethod(payment != null ? payment.getMethod().name() : null)
                .build();
    }
}
