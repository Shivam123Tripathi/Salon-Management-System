package com.salon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * LOGIN REQUEST DTO
 * 
 * Sent by the Android app when a user tries to log in:
 * POST /api/auth/login
 * Body: { "email": "john@example.com", "password": "secret123" }
 * 
 * @NotBlank: Validation annotation — if email or password is null or empty,
 * Spring automatically returns a 400 Bad Request with a descriptive error message.
 * No manual checking needed in our code!
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String emailOtp;

    private String phone;

    private String phoneOtp;
}
