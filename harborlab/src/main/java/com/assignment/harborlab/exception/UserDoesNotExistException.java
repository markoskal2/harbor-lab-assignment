package com.assignment.harborlab.exception;

public class UserDoesNotExistException extends RuntimeException {

    public UserDoesNotExistException(final String message) {
        super(message);
    }
}
