package com.salon.service;

import com.salon.dto.ReportDTO;
import com.salon.entity.Appointment;
import com.salon.enums.AppointmentStatus;
import com.salon.repository.AppointmentRepository;
import com.salon.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REPORT SERVICE
 * 
 * Generates analytics reports:
 * - Revenue by date range
 * - Popular services
 * - Peak hours
 * - Artist performance
 */
@Service
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;

    public ReportService(AppointmentRepository appointmentRepository,
                         PaymentRepository paymentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Generate a comprehensive report for a date range.
     */
    public ReportDTO generateReport(LocalDate startDate, LocalDate endDate) {
        List<Appointment> appointments = appointmentRepository
                .findByAppointmentDateBetween(startDate, endDate);

        List<Appointment> completedAppointments = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED
                        || a.getStatus() == AppointmentStatus.BOOKED)
                .collect(Collectors.toList());

        // Revenue by date
        Map<String, BigDecimal> revenueByDate = completedAppointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAppointmentDate().toString(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                a -> a.getService().getPrice(),
                                BigDecimal::add)));

        // Total revenue
        BigDecimal totalRevenue = completedAppointments.stream()
                .map(a -> a.getService().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Popular services (service name → count)
        Map<String, Long> popularServices = completedAppointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getService().getName(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        // Peak hours (hour → count)
        Map<String, Long> peakHours = completedAppointments.stream()
                .collect(Collectors.groupingBy(
                        a -> String.valueOf(a.getStartTime().getHour()),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        // Artist performance (artist name → count)
        Map<String, Long> artistPerformance = completedAppointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getArtist().getUser().getFullName(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        return ReportDTO.builder()
                .revenueByDate(revenueByDate)
                .totalRevenue(totalRevenue)
                .popularServices(popularServices)
                .peakHours(peakHours)
                .artistPerformance(artistPerformance)
                .build();
    }

    /** Get just popular services report */
    public Map<String, Long> getPopularServices(LocalDate startDate, LocalDate endDate) {
        return generateReport(startDate, endDate).getPopularServices();
    }

    /** Get just peak hours report */
    public Map<String, Long> getPeakHours(LocalDate startDate, LocalDate endDate) {
        return generateReport(startDate, endDate).getPeakHours();
    }
}
