package com.jumbotail.shippingestimator.factory;

import com.jumbotail.shippingestimator.strategy.*;
import org.springframework.stereotype.Component;

/**
 * Factory for selecting the transport strategy based on delivery distance.
 *
 * <ul>
 *   <li>0–100 km   → Mini Van (3 Rs/km/kg)</li>
 *   <li>100–500 km → Truck   (2 Rs/km/kg)</li>
 *   <li>500+ km    → Aeroplane (1 Rs/km/kg)</li>
 * </ul>
 */
@Component
public class TransportModeFactory {

    private final MiniVanStrategy miniVanStrategy;
    private final TruckStrategy truckStrategy;
    private final AeroplaneStrategy aeroplaneStrategy;

    public TransportModeFactory(MiniVanStrategy miniVanStrategy,
                                TruckStrategy truckStrategy,
                                AeroplaneStrategy aeroplaneStrategy) {
        this.miniVanStrategy = miniVanStrategy;
        this.truckStrategy = truckStrategy;
        this.aeroplaneStrategy = aeroplaneStrategy;
    }

    /**
     * Select the transport strategy based on distance.
     */
    public TransportStrategy getStrategy(double distanceKm) {
        if (distanceKm <= 100) {
            return miniVanStrategy;
        } else if (distanceKm <= 500) {
            return truckStrategy;
        } else {
            return aeroplaneStrategy;
        }
    }
}
