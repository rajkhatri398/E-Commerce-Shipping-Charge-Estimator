package com.jumbotail.shippingestimator.strategy;

import org.springframework.stereotype.Component;

/**
 * Mini Van transport strategy for short-distance deliveries (0–100 km).
 * Rate: 3 Rs per km per kg.
 */
@Component
public class MiniVanStrategy implements TransportStrategy {

    private static final double RATE_PER_KM_PER_KG = 3.0;

    @Override
    public double calculateCharge(double distanceKm, double weightKg) {
        return RATE_PER_KM_PER_KG * distanceKm * weightKg;
    }

    @Override
    public String getTransportModeName() {
        return "MINI_VAN";
    }
}
