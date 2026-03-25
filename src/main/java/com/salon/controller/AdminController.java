package com.salon.controller;

import com.salon.dto.*;
import com.salon.service.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * ADMIN CONTROLLER
 * 
 * All endpoints require ADMIN role.
 * @PreAuthorize("hasRole('ADMIN')") is enforced both here AND in SecurityConfig.
 * Double security — defense in depth!
 * 
 * This controller handles:
 * - Dashboard stats
 * - Artist management (CRUD)
 * - Service management (CRUD)
 * - Appointment viewing with filters
 * - Reports & analytics
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DashboardService dashboardService;
    private final ArtistManagementService artistService;
    private final SalonServiceService salonServiceService;
    private final AppointmentService appointmentService;
    private final ReportService reportService;

    public AdminController(DashboardService dashboardService,
                           ArtistManagementService artistService,
                           SalonServiceService salonServiceService,
                           AppointmentService appointmentService,
                           ReportService reportService) {
        this.dashboardService = dashboardService;
        this.artistService = artistService;
        this.salonServiceService = salonServiceService;
        this.appointmentService = appointmentService;
        this.reportService = reportService;
    }

    // ==================== DASHBOARD ====================

    /**
     * GET DASHBOARD STATS
     * 
     * GET /api/admin/dashboard
     * Returns: totalBookings, todayBookings, activeArtists, revenue, recentBookings
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        DashboardDTO dashboard = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
    }

    // ==================== ARTIST MANAGEMENT ====================

    /**
     * ADD A NEW ARTIST
     * 
     * POST /api/admin/artists
     * Body: { "fullName": "Jane Doe", "specialization": "Hair Stylist",
     *         "experienceYears": 5, "workingHoursStart": "09:00", "workingHoursEnd": "18:00" }
     */
    @PostMapping("/artists")
    public ResponseEntity<ApiResponse<ArtistDTO>> addArtist(
            @RequestBody ArtistDTO dto,
            @RequestParam(defaultValue = "artist123") String password) {
        ArtistDTO artist = artistService.addArtist(dto, password);
        return ResponseEntity.ok(ApiResponse.success("Artist added", artist));
    }

    /**
     * UPDATE AN ARTIST
     * 
     * PUT /api/admin/artists/1
     */
    @PutMapping("/artists/{id}")
    public ResponseEntity<ApiResponse<ArtistDTO>> updateArtist(
            @PathVariable Long id, @RequestBody ArtistDTO dto) {
        ArtistDTO artist = artistService.updateArtist(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Artist updated", artist));
    }

    /**
     * REMOVE (DEACTIVATE) AN ARTIST
     * 
     * DELETE /api/admin/artists/1
     */
    @DeleteMapping("/artists/{id}")
    public ResponseEntity<ApiResponse<Void>> removeArtist(@PathVariable Long id) {
        artistService.removeArtist(id);
        return ResponseEntity.ok(ApiResponse.success("Artist removed", null));
    }

    /**
     * ASSIGN SERVICE TO ARTIST
     * 
     * POST /api/admin/artists/1/services/2
     */
    @PostMapping("/artists/{artistId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> assignServiceToArtist(
            @PathVariable Long artistId, @PathVariable Long serviceId) {
        artistService.assignServiceToArtist(artistId, serviceId);
        return ResponseEntity.ok(ApiResponse.success("Service assigned to artist", null));
    }

    /**
     * REMOVE SERVICE FROM ARTIST
     * 
     * DELETE /api/admin/artists/1/services/2
     */
    @DeleteMapping("/artists/{artistId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> removeServiceFromArtist(
            @PathVariable Long artistId, @PathVariable Long serviceId) {
        artistService.removeServiceFromArtist(artistId, serviceId);
        return ResponseEntity.ok(ApiResponse.success("Service removed from artist", null));
    }

    /**
     * ADD LEAVE FOR ARTIST
     * 
     * POST /api/admin/artists/1/leave?date=2024-01-20&reason=Sick
     */
    @PostMapping("/artists/{artistId}/leave")
    public ResponseEntity<ApiResponse<Void>> addArtistLeave(
            @PathVariable Long artistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String reason) {
        artistService.addLeave(artistId, date, reason);
        return ResponseEntity.ok(ApiResponse.success("Leave added", null));
    }

    /**
     * SET BREAK TIME FOR ARTIST
     * 
     * PUT /api/admin/artists/1/break?start=13:00&end=14:00
     */
    @PutMapping("/artists/{artistId}/break")
    public ResponseEntity<ApiResponse<ArtistDTO>> setBreakTime(
            @PathVariable Long artistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end) {
        ArtistDTO artist = artistService.setBreakTime(artistId, start, end);
        return ResponseEntity.ok(ApiResponse.success("Break time updated", artist));
    }

    // ==================== SERVICE MANAGEMENT ====================

    /**
     * ADD A NEW SERVICE
     * 
     * POST /api/admin/services
     * Body: { "name": "Haircut", "description": "...", "durationMinutes": 30,
     *         "price": 500, "category": "Hair" }
     */
    @PostMapping("/services")
    public ResponseEntity<ApiResponse<ServiceDTO>> addService(@RequestBody ServiceDTO dto) {
        ServiceDTO service = salonServiceService.createService(dto);
        return ResponseEntity.ok(ApiResponse.success("Service added", service));
    }

    /**
     * UPDATE A SERVICE
     * 
     * PUT /api/admin/services/1
     */
    @PutMapping("/services/{id}")
    public ResponseEntity<ApiResponse<ServiceDTO>> updateService(
            @PathVariable Long id, @RequestBody ServiceDTO dto) {
        ServiceDTO service = salonServiceService.updateService(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Service updated", service));
    }

    /**
     * DEACTIVATE A SERVICE (soft delete)
     * 
     * DELETE /api/admin/services/1
     */
    @DeleteMapping("/services/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        salonServiceService.deactivateService(id);
        return ResponseEntity.ok(ApiResponse.success("Service deactivated", null));
    }

    /**
     * GET ALL SERVICES (including inactive, for admin view)
     * 
     * GET /api/admin/services
     */
    @GetMapping("/services")
    public ResponseEntity<ApiResponse<List<ServiceDTO>>> getAllServicesAdmin() {
        List<ServiceDTO> services = salonServiceService.getAllServices();
        return ResponseEntity.ok(ApiResponse.success("Services retrieved", services));
    }

    // ==================== APPOINTMENT MANAGEMENT ====================

    /**
     * GET ALL APPOINTMENTS (with optional filters)
     * 
     * GET /api/admin/appointments
     * GET /api/admin/appointments?date=2024-01-15
     * GET /api/admin/appointments?artistId=1
     * GET /api/admin/appointments?status=BOOKED
     * GET /api/admin/appointments?date=2024-01-15&artistId=1&status=BOOKED
     */
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) String status) {
        List<BookingResponse> appointments = appointmentService.getAllAppointments(date, artistId, status);
        return ResponseEntity.ok(ApiResponse.success("Appointments retrieved", appointments));
    }

    // ==================== REPORTS & ANALYTICS ====================

    /**
     * GET REVENUE REPORT
     * 
     * GET /api/admin/reports/revenue?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<ReportDTO>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ReportDTO report = reportService.generateReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Revenue report generated", report));
    }

    /**
     * GET POPULAR SERVICES
     * 
     * GET /api/admin/reports/popular-services?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/reports/popular-services")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getPopularServices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> popular = reportService.getPopularServices(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Popular services retrieved", popular));
    }

    /**
     * GET PEAK HOURS
     * 
     * GET /api/admin/reports/peak-hours?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/reports/peak-hours")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getPeakHours(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> peakHours = reportService.getPeakHours(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Peak hours retrieved", peakHours));
    }
}
