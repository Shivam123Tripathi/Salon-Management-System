package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.dto.BookingRequest;
import com.salon.dto.BookingResponse;
import com.salon.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * APPOINTMENT CONTROLLER
 * 
 * Handles the customer's appointment lifecycle:
 * Book → View → Cancel/Reschedule
 * 
 * All endpoints require authentication.
 * The customer is identified from the JWT token.
 */
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * BOOK AN APPOINTMENT
     * 
     * POST /api/appointments
     * Body: { "artistId": 1, "serviceId": 2, "appointmentDate": "2024-01-15",
     *         "startTime": "10:00", "paymentMethod": "PAY_AT_SALON" }
     * Header: Authorization: Bearer <JWT_TOKEN>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> bookAppointment(
            Authentication authentication, @Valid @RequestBody BookingRequest request) {
        BookingResponse booking = appointmentService.bookAppointment(
                authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Appointment booked successfully", booking));
    }

    /**
     * GET ALL MY APPOINTMENTS
     * 
     * GET /api/appointments
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyAppointments(
            Authentication authentication) {
        List<BookingResponse> appointments = appointmentService
                .getCustomerAppointments(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Appointments retrieved", appointments));
    }

    /**
     * GET UPCOMING APPOINTMENTS
     * 
     * GET /api/appointments/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUpcomingAppointments(
            Authentication authentication) {
        List<BookingResponse> appointments = appointmentService
                .getUpcomingAppointments(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Upcoming appointments retrieved", appointments));
    }

    /**
     * GET PAST APPOINTMENTS
     * 
     * GET /api/appointments/past
     */
    @GetMapping("/past")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getPastAppointments(
            Authentication authentication) {
        List<BookingResponse> appointments = appointmentService
                .getPastAppointments(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Past appointments retrieved", appointments));
    }

    /**
     * CANCEL AN APPOINTMENT
     * 
     * PUT /api/appointments/5/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelAppointment(
            @PathVariable Long id, Authentication authentication) {
        BookingResponse booking = appointmentService
                .cancelAppointment(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled", booking));
    }

    /**
     * RESCHEDULE AN APPOINTMENT
     * 
     * PUT /api/appointments/5/reschedule
     * Body: { "artistId": 1, "serviceId": 2, "appointmentDate": "2024-01-20",
     *         "startTime": "14:00" }
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<ApiResponse<BookingResponse>> rescheduleAppointment(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody BookingRequest request) {
        BookingResponse booking = appointmentService
                .rescheduleAppointment(id, authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Appointment rescheduled", booking));
    }
}
