package com.jumbotail.shippingestimator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Haversine distance calculation utility.
 */
class DistanceCalculatorTest {

    @Test
    @DisplayName("Same point should return 0 distance")
    void testSamePoint() {
        double distance = DistanceCalculator.calculateDistance(12.9716, 77.5946, 12.9716, 77.5946);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    @DisplayName("Bangalore to Mumbai should be approximately 845 km")
    void testBangaloreToMumbai() {
        // Bangalore: 12.9716, 77.5946
        // Mumbai: 19.0760, 72.8777
        double distance = DistanceCalculator.calculateDistance(12.9716, 77.5946, 19.0760, 72.8777);
        // Expected ~845 km
        assertTrue(distance > 800 && distance < 900,
                "Expected distance between 800 and 900 km, got: " + distance);
    }

    @Test
    @DisplayName("Bangalore to Delhi should be approximately 1740 km")
    void testBangaloreToDelhi() {
        // Bangalore: 12.9716, 77.5946
        // Delhi: 28.7041, 77.1025
        double distance = DistanceCalculator.calculateDistance(12.9716, 77.5946, 28.7041, 77.1025);
        // Expected ~1740 km
        assertTrue(distance > 1700 && distance < 1800,
                "Expected distance between 1700 and 1800 km, got: " + distance);
    }

    @Test
    @DisplayName("Short distance within same city should be < 50 km")
    void testShortDistance() {
        // Two points within Bangalore
        double distance = DistanceCalculator.calculateDistance(12.9716, 77.5946, 12.9352, 77.6245);
        assertTrue(distance < 50,
                "Expected short distance < 50 km, got: " + distance);
    }

    @Test
    @DisplayName("Distance should be symmetric (A→B == B→A)")
    void testSymmetry() {
        double distAB = DistanceCalculator.calculateDistance(12.9716, 77.5946, 19.0760, 72.8777);
        double distBA = DistanceCalculator.calculateDistance(19.0760, 72.8777, 12.9716, 77.5946);
        assertEquals(distAB, distBA, 0.001);
    }

    @Test
    @DisplayName("Known distance: London to Paris ~343 km")
    void testLondonToParis() {
        // London: 51.5074, -0.1278
        // Paris: 48.8566, 2.3522
        double distance = DistanceCalculator.calculateDistance(51.5074, -0.1278, 48.8566, 2.3522);
        assertTrue(distance > 330 && distance < 360,
                "Expected distance between 330 and 360 km, got: " + distance);
    }
}
