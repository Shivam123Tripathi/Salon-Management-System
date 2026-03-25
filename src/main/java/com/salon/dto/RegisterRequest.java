package com.salon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * REGISTER REQUEST DTO
 * 
 * Sent when a new user creates an account:
 * POST /api/auth/register
 * Body: { "fullName": "John", "email": "john@example.com", "password": "secret123", "phone": "9876543210" }
 * 
 * @Size(min=6): Password must be at least 6 characters for security.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phone;
    private String address;
    private String emailOtp;
    private String phoneOtp;
}
