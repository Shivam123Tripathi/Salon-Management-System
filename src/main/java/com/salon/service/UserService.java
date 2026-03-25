package com.salon.service;

import com.salon.dto.UserProfileDTO;
import com.salon.entity.User;
import com.salon.exception.ResourceNotFoundException;
import com.salon.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * USER SERVICE
 * 
 * Handles user profile operations (view, update).
 * The "current user" is identified from the JWT token (via email).
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get user profile by email (extracted from JWT token in the controller).
     */
    public UserProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToDTO(user);
    }

    /**
     * Update user profile.
     * Only allows updating: fullName, phone, address.
     * Email and role CANNOT be changed via this endpoint.
     */
    public UserProfileDTO updateProfile(String email, UserProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());

        User updated = userRepository.save(user);
        return mapToDTO(updated);
    }

    /** Helper: Convert User entity → UserProfileDTO */
    private UserProfileDTO mapToDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole().name())
                .build();
    }
}
