package com.jumbotail.shippingestimator.util;

/**
 * Utility class for geo-distance calculations using the Haversine formula.
 *
 * <p>The Haversine formula determines the great-circle distance between two points
 * on a sphere given their longitudes and latitudes. This is essential for calculating
 * the real-world distance between warehouses, sellers, and customers.</p>
 */
public final class DistanceCalculator {

    /** Earth's mean radius in kilometers */
    private static final double EARTH_RADIUS_KM = 6371.0;

    // Private constructor to prevent instantiation
    private DistanceCalculator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Calculate the distance between two geographic coordinates using the Haversine formula.
     *
     * <p>Formula:
     * a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlng/2)
     * c = 2 × atan2(√a, √(1−a))
     * d = R × c
     * </p>
     *
     * @param lat1 latitude of point 1 in degrees
     * @param lng1 longitude of point 1 in degrees
     * @param lat2 latitude of point 2 in degrees
     * @param lng2 longitude of point 2 in degrees
     * @return distance between the two points in kilometers
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLatRad = Math.toRadians(lat2 - lat1);
        double deltaLngRad = Math.toRadians(lng2 - lng1);

        // Haversine formula
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2)
                 + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                 * Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
