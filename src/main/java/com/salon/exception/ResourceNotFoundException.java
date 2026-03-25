package com.salon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource doesn't exist in the database.
 * 
 * @ResponseStatus(NOT_FOUND): Automatically returns HTTP 404 status.
 * Example: Finding an artist with ID 999 that doesn't exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
