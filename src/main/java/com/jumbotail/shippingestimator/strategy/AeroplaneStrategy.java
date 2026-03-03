package com.jumbotail.shippingestimator.strategy;

import org.springframework.stereotype.Component;

/**
 * Aeroplane transport strategy for long-distance deliveries (500+ km).
 * Rate: 1 Rs/km/kg.
 */
@Component
public class AeroplaneStrategy implements TransportStrategy {

    private static final double RATE_PER_KM_PER_KG = 1.0;

    @Override
    public double calculateCharge(double distanceKm, double weightKg) {
        return RATE_PER_KM_PER_KG * distanceKm * weightKg;
    }

    @Override
    public String getTransportModeName() {
        return "AEROPLANE";
    }
}
