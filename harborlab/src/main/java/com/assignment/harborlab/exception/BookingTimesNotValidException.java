package com.assignment.harborlab.exception;

public class BookingTimesNotValidException extends RuntimeException {

    public BookingTimesNotValidException(final String message) {
        super(message);
    }
}
