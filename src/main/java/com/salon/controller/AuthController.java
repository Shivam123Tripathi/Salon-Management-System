package com.salon.controller;

import com.salon.dto.*;
import com.salon.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AUTHENTICATION CONTROLLER
 * 
 * Handles login and registration.
 * All endpoints here are PUBLIC (no JWT required) — configured in SecurityConfig.
 * 
 * @RestController: Combines @Controller + @ResponseBody
 *   - Every method returns JSON data (not an HTML page)
 * 
 * @RequestMapping("/api/auth"): All endpoints in this class start with /api/auth
 * 
 * @CrossOrigin: Allows requests from any origin (for development).
 *   In production, restrict this to your Android app's domain.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * REGISTER A NEW CUSTOMER
     * 
     * POST /api/auth/register
     * Body: { "fullName": "John", "email": "john@example.com", "password": "secret123" }
     * 
     * @Valid: Triggers validation annotations on RegisterRequest (@NotBlank, @Email, @Size)
     * If validation fails, GlobalExceptionHandler catches it and returns field-level errors.
     * 
     * @RequestBody: Tells Spring to parse the JSON request body into a RegisterRequest object.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/otp/request")
    public ResponseEntity<ApiResponse<OtpIssueResponse>> requestOtp(@Valid @RequestBody OtpRequest request) {
        authService.requestOtp(request.getChannel(), request.getTarget());
        OtpIssueResponse response = OtpIssueResponse.builder()
                .channel(request.getChannel())
                .target(request.getTarget())
                .expiresInSeconds(300)
                .build();
        return ResponseEntity.ok(ApiResponse.success("OTP sent", response));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        authService.verifyOtp(request.getChannel(), request.getTarget(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("OTP verified", null));
    }

    /**
     * REGISTER A NEW ADMIN
     * 
     * POST /api/auth/register/admin
     * Same as customer registration but creates an ADMIN role user.
     */
    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerAdmin(request);
        return ResponseEntity.ok(ApiResponse.success("Admin registration successful", response));
    }

    /**
     * LOGIN
     * 
     * POST /api/auth/login
     * Body: { "email": "john@example.com", "password": "secret123" }
     * Returns: JWT token + user info
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
