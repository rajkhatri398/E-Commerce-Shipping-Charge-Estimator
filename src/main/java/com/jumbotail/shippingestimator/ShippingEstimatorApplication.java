package com.jumbotail.shippingestimator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Shipping Estimator application.
 *
 * <p>This is a B2B e-commerce shipping charge calculator for Kirana stores.
 * It calculates shipping charges based on distance, transport mode, product weight,
 * and delivery speed.</p>
 *
 * <p>To run:
 * <pre>
 *   mvn clean install
 *   mvn spring-boot:run
 * </pre>
 * </p>
 */
@SpringBootApplication
public class ShippingEstimatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShippingEstimatorApplication.class, args);
    }
}
