package com.salon.dto;

import lombok.*;

/**
 * AUTH RESPONSE DTO
 * 
 * Returned after successful login or registration:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",  ← JWT token
 *   "userId": 1,
 *   "fullName": "John Doe",
 *   "email": "john@example.com",
 *   "role": "CUSTOMER"
 * }
 * 
 * The Android app stores the "token" and sends it in the Authorization header
 * for all subsequent requests: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
}
