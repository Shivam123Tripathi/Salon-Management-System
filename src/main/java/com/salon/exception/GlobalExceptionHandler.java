package com.salon.exception;

import com.salon.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GLOBAL EXCEPTION HANDLER
 * 
 * @RestControllerAdvice: This class acts as a CATCH-ALL for exceptions thrown by any controller.
 * 
 * Without this, Spring Boot returns ugly HTML error pages or generic JSON.
 * With this, we return clean, consistent JSON error responses that the Android app can parse.
 * 
 * HOW IT WORKS:
 * 1. A controller method throws an exception (e.g., ResourceNotFoundException)
 * 2. Spring intercepts it BEFORE sending the response
 * 3. It looks for a matching @ExceptionHandler method here
 * 4. That method creates a clean ApiResponse with the error message
 * 5. The clean JSON response is sent to the client
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles: ResourceNotFoundException (HTTP 404)
     * Example: GET /api/artists/999 when artist 999 doesn't exist
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles: BadRequestException (HTTP 400)
     * Example: POST /api/auth/register with an email that already exists
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles: SlotNotAvailableException (HTTP 409 Conflict)
     * Example: Trying to book a slot that's already taken
     */
    @ExceptionHandler(SlotNotAvailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleSlotNotAvailable(SlotNotAvailableException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles: Validation errors from @Valid annotation (HTTP 400)
     * 
     * When a DTO has @NotBlank, @Email, @Size etc., and the input fails validation,
     * Spring throws MethodArgumentNotValidException. We catch it here and return
     * a map of field → error message.
     * 
     * Example response:
     * { "success": false, "message": "Validation failed", "data": {"email": "must not be blank"} }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    /**
     * Handles: Any unexpected exception that we didn't specifically handle above.
     * This is a safety net — we never want raw stack traces going to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + ex.getMessage()));
    }
}
