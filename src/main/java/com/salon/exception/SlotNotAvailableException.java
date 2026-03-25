package com.salon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the requested time slot is already booked or blocked.
 * 
 * @ResponseStatus(CONFLICT): Automatically returns HTTP 409 status.
 * HTTP 409 Conflict is the correct status for "this action conflicts with existing state."
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class SlotNotAvailableException extends RuntimeException {
    public SlotNotAvailableException(String message) {
        super(message);
    }
}
