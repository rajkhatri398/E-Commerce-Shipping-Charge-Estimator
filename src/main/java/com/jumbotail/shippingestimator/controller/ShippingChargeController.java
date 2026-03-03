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
 * REST Controller for shipping charge calculation APIs.
 *
 * <p>Provides endpoints to calculate shipping charges based on
 * warehouse-customer distance, product weight, and delivery speed.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/shipping-charge")
@RequiredArgsConstructor
public class ShippingChargeController {

    private final ShippingChargeService shippingChargeService;

    /**
     * API 2: Get shipping charge from a warehouse to a customer.
     *
     * <p>Endpoint: GET /api/v1/shipping-charge?warehouseId=1&customerId=10&productId=5&deliverySpeed=standard</p>
     *
     * @param warehouseId   the source warehouse ID
     * @param customerId    the destination customer ID
     * @param productId     the product ID (for weight calculation)
     * @param deliverySpeed delivery speed: "standard" or "express"
     * @return the calculated shipping charge
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
     * API 3: Calculate end-to-end shipping charge.
     * Finds the nearest warehouse and calculates the shipping charge in one call.
     *
     * <p>Endpoint: POST /api/v1/shipping-charge/calculate</p>
     *
     * @param request the shipping calculation request body
     * @return the shipping charge along with nearest warehouse details
     */
    @PostMapping("/calculate")
    public ResponseEntity<ShippingCalculateResponse> calculateShippingCharge(
            @Valid @RequestBody ShippingCalculateRequest request) {
        log.info("POST /api/v1/shipping-charge/calculate - request={}", request);
        ShippingCalculateResponse response = shippingChargeService.calculateShippingCharge(request);
        return ResponseEntity.ok(response);
    }
}
