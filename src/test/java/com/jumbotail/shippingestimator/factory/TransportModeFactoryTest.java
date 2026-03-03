package com.jumbotail.shippingestimator.factory;

import com.jumbotail.shippingestimator.strategy.AeroplaneStrategy;
import com.jumbotail.shippingestimator.strategy.MiniVanStrategy;
import com.jumbotail.shippingestimator.strategy.TransportStrategy;
import com.jumbotail.shippingestimator.strategy.TruckStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportModeFactoryTest {

    private TransportModeFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TransportModeFactory(
                new MiniVanStrategy(),
                new TruckStrategy(),
                new AeroplaneStrategy()
        );
    }

    @Test
    @DisplayName("Distance 0 km should select MiniVan")
    void testZeroDistanceSelectsMiniVan() {
        TransportStrategy strategy = factory.getStrategy(0);
        assertInstanceOf(MiniVanStrategy.class, strategy);
        assertEquals("MINI_VAN", strategy.getTransportModeName());
    }

    @Test
    @DisplayName("Distance 50 km should select MiniVan")
    void testShortDistanceSelectsMiniVan() {
        TransportStrategy strategy = factory.getStrategy(50);
        assertInstanceOf(MiniVanStrategy.class, strategy);
    }

    @Test
    @DisplayName("Distance 100 km (boundary) should select MiniVan")
    void testBoundary100SelectsMiniVan() {
        TransportStrategy strategy = factory.getStrategy(100);
        assertInstanceOf(MiniVanStrategy.class, strategy);
    }

    @Test
    @DisplayName("Distance 101 km should select Truck")
    void testAbove100SelectsTruck() {
        TransportStrategy strategy = factory.getStrategy(101);
        assertInstanceOf(TruckStrategy.class, strategy);
        assertEquals("TRUCK", strategy.getTransportModeName());
    }

    @Test
    @DisplayName("Distance 300 km should select Truck")
    void testMediumDistanceSelectsTruck() {
        TransportStrategy strategy = factory.getStrategy(300);
        assertInstanceOf(TruckStrategy.class, strategy);
    }

    @Test
    @DisplayName("Distance 500 km (boundary) should select Truck")
    void testBoundary500SelectsTruck() {
        TransportStrategy strategy = factory.getStrategy(500);
        assertInstanceOf(TruckStrategy.class, strategy);
    }

    @Test
    @DisplayName("Distance 501 km should select Aeroplane")
    void testAbove500SelectsAeroplane() {
        TransportStrategy strategy = factory.getStrategy(501);
        assertInstanceOf(AeroplaneStrategy.class, strategy);
        assertEquals("AEROPLANE", strategy.getTransportModeName());
    }

    @Test
    @DisplayName("Distance 1000 km should select Aeroplane")
    void testLongDistanceSelectsAeroplane() {
        TransportStrategy strategy = factory.getStrategy(1000);
        assertInstanceOf(AeroplaneStrategy.class, strategy);
    }

    // Charge calculation tests

    @Test
    @DisplayName("MiniVan: 50 km × 2 kg = 3×50×2 = 300 Rs")
    void testMiniVanChargeCalculation() {
        TransportStrategy strategy = factory.getStrategy(50);
        double charge = strategy.calculateCharge(50, 2);
        assertEquals(300.0, charge, 0.001);
    }

    @Test
    @DisplayName("Truck: 200 km × 5 kg = 2×200×5 = 2000 Rs")
    void testTruckChargeCalculation() {
        TransportStrategy strategy = factory.getStrategy(200);
        double charge = strategy.calculateCharge(200, 5);
        assertEquals(2000.0, charge, 0.001);
    }

    @Test
    @DisplayName("Aeroplane: 600 km × 3 kg = 1×600×3 = 1800 Rs")
    void testAeroplaneChargeCalculation() {
        TransportStrategy strategy = factory.getStrategy(600);
        double charge = strategy.calculateCharge(600, 3);
        assertEquals(1800.0, charge, 0.001);
    }
}
