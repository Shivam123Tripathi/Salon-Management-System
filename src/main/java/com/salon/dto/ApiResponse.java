package com.salon.dto;

import lombok.*;

/**
 * GENERIC API RESPONSE WRAPPER
 * 
 * Every API response from our server follows the same structure:
 * {
 *   "success": true/false,
 *   "message": "Some human-readable message",
 *   "data": { ... actual data ... }
 * }
 * 
 * WHY? Consistency. The Android app always knows the response format.
 * It checks "success" first, shows "message" if there's an error,
 * and parses "data" for the actual content.
 * 
 * The <T> is a GENERIC: T can be any type (UserDTO, List<ServiceDTO>, etc.)
 * This avoids creating separate response classes for each endpoint.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    /** Convenience factory method for success responses */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /** Convenience factory method for error responses */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}
