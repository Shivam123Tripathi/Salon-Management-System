package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.dto.UserProfileDTO;
import com.salon.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * CUSTOMER CONTROLLER
 * 
 * Manages customer profile operations.
 * All endpoints require authentication (JWT token).
 * 
 * HOW WE GET THE CURRENT USER:
 * The Authentication object is injected by Spring Security.
 * It's populated by our JwtAuthenticationFilter.
 * authentication.getName() returns the email (which we set as the subject in the JWT).
 */
@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final UserService userService;

    public CustomerController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET MY PROFILE
     * 
     * GET /api/customer/profile
     * Header: Authorization: Bearer <JWT_TOKEN>
     * 
     * The token tells us WHO is making the request.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile(Authentication authentication) {
        UserProfileDTO profile = userService.getProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profile));
    }

    /**
     * UPDATE MY PROFILE
     * 
     * PUT /api/customer/profile
     * Body: { "fullName": "New Name", "phone": "9876543210", "address": "New Address" }
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            Authentication authentication, @RequestBody UserProfileDTO dto) {
        UserProfileDTO updated = userService.updateProfile(authentication.getName(), dto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", updated));
    }
}
