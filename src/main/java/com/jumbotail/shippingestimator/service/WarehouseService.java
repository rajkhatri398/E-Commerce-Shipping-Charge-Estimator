package com.jumbotail.shippingestimator.service;

import com.jumbotail.shippingestimator.dto.NearestWarehouseResponse;

/**
 * Service interface for warehouse-related operations.
 */
public interface WarehouseService {

    NearestWarehouseResponse findNearestWarehouse(Long sellerId, Long productId);
}
