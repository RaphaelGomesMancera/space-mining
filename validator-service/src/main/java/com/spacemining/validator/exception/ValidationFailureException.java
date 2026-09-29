package com.spacemining.validator.exception;

public class ValidationFailureException extends RuntimeException {

    public ValidationFailureException(String message) {
        super(message);
    }
}
