package com.salon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the client sends invalid data.
 * 
 * @ResponseStatus(BAD_REQUEST): Automatically returns HTTP 400 status.
 * Example: Trying to register with an email that already exists.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
