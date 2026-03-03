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
 * Finds the nearest warehouse to a seller using the Haversine formula.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    @Cacheable(value = "nearestWarehouse", key = "#sellerId + '-' + #productId")
    public NearestWarehouseResponse findNearestWarehouse(Long sellerId, Long productId) {
        log.info("Finding nearest warehouse for sellerId={}, productId={}", sellerId, productId);

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", sellerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (!product.getSellerId().equals(sellerId)) {
            throw new ResourceNotFoundException(
                    String.format("Product %d does not belong to Seller %d", productId, sellerId));
        }

        List<Warehouse> warehouses = warehouseRepository.findByActiveTrue();
        if (warehouses.isEmpty()) {
            throw new NoWarehouseAvailableException("No active warehouses available in the system");
        }

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

        return NearestWarehouseResponse.builder()
                .warehouseId(nearest.getId())
                .warehouseLocation(WarehouseLocationDto.builder()
                        .lat(nearest.getLatitude())
                        .lng(nearest.getLongitude())
                        .build())
                .build();
    }
}
