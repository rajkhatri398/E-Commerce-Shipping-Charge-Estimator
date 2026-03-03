package com.jumbotail.shippingestimator.controller;

import com.jumbotail.shippingestimator.dto.NearestWarehouseResponse;
import com.jumbotail.shippingestimator.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for warehouse-related APIs.
 *
 * <p>Provides endpoints to find the nearest warehouse for a seller.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * API 1: Get the nearest warehouse for a seller.
     *
     * <p>Endpoint: GET /api/v1/warehouse/nearest?sellerId=123&productId=456</p>
     *
     * @param sellerId  the seller's ID
     * @param productId the product's ID
     * @return the nearest warehouse with its location
     */
    @GetMapping("/nearest")
    public ResponseEntity<NearestWarehouseResponse> getNearestWarehouse(
            @RequestParam Long sellerId,
            @RequestParam Long productId) {
        log.info("GET /api/v1/warehouse/nearest - sellerId={}, productId={}", sellerId, productId);
        NearestWarehouseResponse response = warehouseService.findNearestWarehouse(sellerId, productId);
        return ResponseEntity.ok(response);
    }
}
