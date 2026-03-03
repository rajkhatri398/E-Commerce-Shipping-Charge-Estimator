package com.jumbotail.shippingestimator.exception;

/**
 * Exception thrown when no warehouses are available in the system.
 */
public class NoWarehouseAvailableException extends RuntimeException {

    public NoWarehouseAvailableException(String message) {
        super(message);
    }
}
