package com.assignment.harborlab.exception;

public class RoomExistsException extends RuntimeException {

    public RoomExistsException(final String message) {
        super(message);
    }
}
