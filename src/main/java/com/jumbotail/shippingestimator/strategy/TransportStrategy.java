package com.jumbotail.shippingestimator.strategy;

/**
 * Strategy interface for transport mode-based shipping charge calculation.
 * Each concrete strategy implements a different transport mode with its own rate.
 *
 * <p>Strategy Pattern: Defines a family of algorithms (transport modes),
 * encapsulates each one, and makes them interchangeable.</p>
 */
public interface TransportStrategy {

    /**
     * Calculate the shipping charge based on distance and weight.
     *
     * @param distanceKm distance between warehouse and customer in kilometers
     * @param weightKg   weight of the product in kilograms
     * @return the calculated transport charge in INR
     */
    double calculateCharge(double distanceKm, double weightKg);

    /**
     * Get the name of the transport mode.
     *
     * @return transport mode name
     */
    String getTransportModeName();
}
