package com.jumbotail.shippingestimator.strategy;

import org.springframework.stereotype.Component;

/**
 * Truck transport strategy for medium-distance deliveries (100–500 km).
 * Rate: 2 Rs per km per kg.
 */
@Component
public class TruckStrategy implements TransportStrategy {

    private static final double RATE_PER_KM_PER_KG = 2.0;

    @Override
    public double calculateCharge(double distanceKm, double weightKg) {
        return RATE_PER_KM_PER_KG * distanceKm * weightKg;
    }

    @Override
    public String getTransportModeName() {
        return "TRUCK";
    }
}
