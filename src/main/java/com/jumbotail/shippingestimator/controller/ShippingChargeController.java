package com.jumbotail.shippingestimator.controller;

import com.jumbotail.shippingestimator.dto.ShippingCalculateRequest;
import com.jumbotail.shippingestimator.dto.ShippingCalculateResponse;
import com.jumbotail.shippingestimator.dto.ShippingChargeResponse;
import com.jumbotail.shippingestimator.service.ShippingChargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for shipping charge calculation APIs.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/shipping-charge")
@RequiredArgsConstructor
public class ShippingChargeController {

    private final ShippingChargeService shippingChargeService;

    /**
     * API 2: Calculate shipping charge from a warehouse to a customer.
     */
    @GetMapping
    public ResponseEntity<ShippingChargeResponse> getShippingCharge(
            @RequestParam Long warehouseId,
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam String deliverySpeed) {
        log.info("GET /api/v1/shipping-charge - warehouseId={}, customerId={}, productId={}, speed={}",
                warehouseId, customerId, productId, deliverySpeed);
        ShippingChargeResponse response = shippingChargeService.getShippingCharge(
                warehouseId, customerId, productId, deliverySpeed);
        return ResponseEntity.ok(response);
    }

    /**
     * API 3: End-to-end shipping charge calculation.
     * Finds the nearest warehouse and computes the shipping charge in one call.
     */
    @PostMapping("/calculate")
    public ResponseEntity<ShippingCalculateResponse> calculateShippingCharge(
            @Valid @RequestBody ShippingCalculateRequest request) {
        log.info("POST /api/v1/shipping-charge/calculate - request={}", request);
        ShippingCalculateResponse response = shippingChargeService.calculateShippingCharge(request);
        return ResponseEntity.ok(response);
    }
}
