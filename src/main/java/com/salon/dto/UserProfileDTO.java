package com.salon.dto;

import lombok.*;

/**
 * USER PROFILE DTO
 * 
 * Used for viewing and updating the customer's profile.
 * Notice: NO password field here — we never send passwords back in API responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String role;
}
