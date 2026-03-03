package com.jumbotail.shippingestimator.service.impl;

import com.jumbotail.shippingestimator.dto.NearestWarehouseResponse;
import com.jumbotail.shippingestimator.dto.WarehouseLocationDto;
import com.jumbotail.shippingestimator.exception.NoWarehouseAvailableException;
import com.jumbotail.shippingestimator.exception.ResourceNotFoundException;
import com.jumbotail.shippingestimator.model.Product;
import com.jumbotail.shippingestimator.model.Seller;
import com.jumbotail.shippingestimator.model.Warehouse;
import com.jumbotail.shippingestimator.repository.ProductRepository;
import com.jumbotail.shippingestimator.repository.SellerRepository;
import com.jumbotail.shippingestimator.repository.WarehouseRepository;
import com.jumbotail.shippingestimator.service.WarehouseService;
import com.jumbotail.shippingestimator.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of WarehouseService.
 * Finds the nearest warehouse to a seller's location using the Haversine formula.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Find the nearest warehouse to a seller.
     *
     * <p>Logic:
     * 1. Validate that seller and product exist
     * 2. Fetch all active warehouses
     * 3. Calculate distance from seller to each warehouse using Haversine formula
     * 4. Return the warehouse with the minimum distance
     * </p>
     *
     * @param sellerId  the seller's ID
     * @param productId the product's ID
     * @return the nearest warehouse details
     */
    @Override
    @Cacheable(value = "nearestWarehouse", key = "#sellerId + '-' + #productId")
    public NearestWarehouseResponse findNearestWarehouse(Long sellerId, Long productId) {
        log.info("Finding nearest warehouse for sellerId={}, productId={}", sellerId, productId);

        // Step 1: Validate seller exists
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", sellerId));

        // Step 2: Validate product exists and belongs to seller
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (!product.getSellerId().equals(sellerId)) {
            throw new ResourceNotFoundException(
                    String.format("Product %d does not belong to Seller %d", productId, sellerId));
        }

        // Step 3: Fetch all active warehouses
        List<Warehouse> warehouses = warehouseRepository.findByActiveTrue();
        if (warehouses.isEmpty()) {
            throw new NoWarehouseAvailableException("No active warehouses available in the system");
        }

        // Step 4: Find the nearest warehouse using Haversine distance
        Warehouse nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Warehouse warehouse : warehouses) {
            double distance = DistanceCalculator.calculateDistance(
                    seller.getLatitude(), seller.getLongitude(),
                    warehouse.getLatitude(), warehouse.getLongitude()
            );
            log.debug("Distance from seller to warehouse '{}': {} km", warehouse.getName(), distance);

            if (distance < minDistance) {
                minDistance = distance;
                nearest = warehouse;
            }
        }

        log.info("Nearest warehouse: {} (id={}, distance={} km)",
                nearest.getName(), nearest.getId(), minDistance);

        // Step 5: Build and return response
        return NearestWarehouseResponse.builder()
                .warehouseId(nearest.getId())
                .warehouseLocation(WarehouseLocationDto.builder()
                        .lat(nearest.getLatitude())
                        .lng(nearest.getLongitude())
                        .build())
                .build();
    }
}
