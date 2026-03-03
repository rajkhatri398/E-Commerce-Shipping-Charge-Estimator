package com.jumbotail.shippingestimator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check and service info controller.
 */
@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "service", "Shipping Estimator API",
                "status", "UP",
                "version", "1.0.0",
                "endpoints", Map.of(
                        "nearestWarehouse", "GET /api/v1/warehouse/nearest?sellerId={id}&productId={id}",
                        "shippingCharge", "GET /api/v1/shipping-charge?warehouseId={id}&customerId={id}&productId={id}&deliverySpeed={standard|express}",
                        "calculateShipping", "POST /api/v1/shipping-charge/calculate"
                )
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
