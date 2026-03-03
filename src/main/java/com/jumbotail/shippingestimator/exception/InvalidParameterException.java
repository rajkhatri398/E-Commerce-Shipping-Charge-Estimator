package com.jumbotail.shippingestimator.exception;

/**
 * Thrown when an invalid parameter value is provided.
 */
public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String message) {
        super(message);
    }
}
