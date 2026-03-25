package com.salon.service;

import com.salon.dto.AuthResponse;
import com.salon.dto.LoginRequest;
import com.salon.dto.RegisterRequest;
import com.salon.entity.User;
import com.salon.enums.UserRole;
import com.salon.exception.BadRequestException;
import com.salon.repository.UserRepository;
import com.salon.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AUTHENTICATION SERVICE
 * 
 * Handles user registration and login.
 * 
 * REGISTRATION FLOW:
 * 1. Check if email already exists → throw error if yes
 * 2. Hash the password with BCrypt (NEVER store plain text!)
 * 3. Save user to database
 * 4. Generate JWT token
 * 5. Return token + user info
 * 
 * LOGIN FLOW:
 * 1. AuthenticationManager verifies email + password against database
 * 2. If valid → generate JWT token
 * 3. Return token + user info
 * 4. If invalid → Spring Security throws BadCredentialsException
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final boolean requirePhoneOtp;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       OtpService otpService,
                       @Value("${auth.require-phone-otp:false}") boolean requirePhoneOtp) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
        this.requirePhoneOtp = requirePhoneOtp;
    }

    /**
     * Register a new customer account.
     */
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        request.setEmail(normalizedEmail);
        verifyPhoneOtpIfRequired(request.getPhone(), request.getPhoneOtp());

        // 1. Check if email is already taken
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already registered. Please use a different email.");
        }

        // 2. Check if phone is already taken (if provided)
        if (request.getPhone() != null && !request.getPhone().isEmpty()
                && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number is already registered.");
        }

        // 3. Build the User entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(UserRole.CUSTOMER)  // Default role is CUSTOMER
                .build();

        // 4. Save to database (JPA auto-generates the INSERT SQL)
        User savedUser = userRepository.save(user);

        // 5. Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        // 6. Build and return the response
        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    /**
     * Register a new admin account.
     * In production, this would be restricted or use an invitation system.
     */
    public AuthResponse registerAdmin(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        request.setEmail(normalizedEmail);
        verifyPhoneOtpIfRequired(request.getPhone(), request.getPhoneOtp());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already registered.");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(UserRole.ADMIN)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    /**
     * Authenticate a user and return a JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        request.setEmail(normalizedEmail);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("User not found"));
        verifyPhoneOtpIfRequired(user.getPhone(), request.getPhoneOtp());

        // AuthenticationManager does the heavy lifting:
        // 1. Calls CustomUserDetailsService.loadUserByUsername(email)
        // 2. Compares provided password with stored BCrypt hash
        // 3. Throws BadCredentialsException if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        // If we get here, credentials are valid
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public String requestOtp(String channel, String target) {
        return otpService.issueOtp(channel, target);
    }

    public void verifyOtp(String channel, String target, String otp) {
        otpService.verifyOtp(channel, target, otp);
    }

    private void verifyPhoneOtpIfRequired(String phone, String phoneOtp) {
        if (!requirePhoneOtp) {
            return;
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new BadRequestException("Phone number is required for OTP verification.");
        }
        if (phoneOtp == null || phoneOtp.trim().isEmpty()) {
            throw new BadRequestException("Phone OTP is required.");
        }
        otpService.verifyOtp("PHONE", phone, phoneOtp);
    }
}
