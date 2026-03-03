package com.jumbotail.shippingestimator.controller;

import com.jumbotail.shippingestimator.dto.NearestWarehouseResponse;
import com.jumbotail.shippingestimator.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for warehouse-related APIs.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * API 1: Get the nearest warehouse for a seller.
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
