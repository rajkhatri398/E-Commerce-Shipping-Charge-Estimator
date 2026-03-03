package com.jumbotail.shippingestimator.service;

import com.jumbotail.shippingestimator.dto.NearestWarehouseResponse;

/**
 * Service interface for warehouse-related operations.
 */
public interface WarehouseService {

    /**
     * Find the nearest warehouse to a given seller.
     *
     * @param sellerId  the seller's ID
     * @param productId the product's ID (for reference/validation)
     * @return the nearest warehouse details
     */
    NearestWarehouseResponse findNearestWarehouse(Long sellerId, Long productId);
}
