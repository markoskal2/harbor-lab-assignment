package com.assignment.harborlab.exception;

public class UserExistsException extends RuntimeException {

    public UserExistsException(final String message) {
        super(message);
    }
}
