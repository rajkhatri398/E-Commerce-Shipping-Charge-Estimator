package com.jumbotail.shippingestimator.service;

import com.jumbotail.shippingestimator.dto.ShippingCalculateRequest;
import com.jumbotail.shippingestimator.dto.ShippingCalculateResponse;
import com.jumbotail.shippingestimator.dto.ShippingChargeResponse;

/**
 * Service interface for shipping charge calculations.
 */
public interface ShippingChargeService {

    ShippingChargeResponse getShippingCharge(Long warehouseId, Long customerId,
                                             Long productId, String deliverySpeed);

    ShippingCalculateResponse calculateShippingCharge(ShippingCalculateRequest request);
}
