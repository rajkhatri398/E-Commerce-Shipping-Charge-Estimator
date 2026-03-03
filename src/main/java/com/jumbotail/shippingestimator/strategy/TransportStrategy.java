package com.jumbotail.shippingestimator.strategy;

/**
 * Strategy interface for transport mode-based shipping charge calculation.
 */
public interface TransportStrategy {

    double calculateCharge(double distanceKm, double weightKg);

    String getTransportModeName();
}
