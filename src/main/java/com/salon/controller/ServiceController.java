package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.dto.ServiceDTO;
import com.salon.service.SalonServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SERVICE CONTROLLER
 * 
 * Lists salon services (haircut, facial, etc.).
 * GET endpoints are PUBLIC (customers can browse without login).
 */
@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceController {

    private final SalonServiceService salonServiceService;

    public ServiceController(SalonServiceService salonServiceService) {
        this.salonServiceService = salonServiceService;
    }

    /**
     * LIST ALL ACTIVE SERVICES
     * 
     * GET /api/services
     * Public endpoint — no authentication needed.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceDTO>>> getAllServices() {
        List<ServiceDTO> services = salonServiceService.getAllActiveServices();
        return ResponseEntity.ok(ApiResponse.success("Services retrieved", services));
    }

    /**
     * GET SERVICE BY ID
     * 
     * GET /api/services/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDTO>> getServiceById(@PathVariable Long id) {
        ServiceDTO service = salonServiceService.getServiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Service retrieved", service));
    }

    /**
     * GET SERVICES BY CATEGORY
     * 
     * GET /api/services/category/Hair
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ServiceDTO>>> getServicesByCategory(
            @PathVariable String category) {
        List<ServiceDTO> services = salonServiceService.getServicesByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Services retrieved", services));
    }
}
