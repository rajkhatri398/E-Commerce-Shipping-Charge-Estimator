package com.jumbotail.shippingestimator.exception;

/**
 * Exception thrown when an invalid parameter is provided to an API.
 * Examples: invalid delivery speed, missing required parameters.
 */
public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String message) {
        super(message);
    }
}
