package com.assignment.harborlab.exception;

public class BookingConflictException extends RuntimeException {

    public BookingConflictException(final String message) {
        super(message);
    }
}
