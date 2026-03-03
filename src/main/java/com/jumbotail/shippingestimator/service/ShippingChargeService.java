package com.jumbotail.shippingestimator.service;

import com.jumbotail.shippingestimator.dto.ShippingCalculateRequest;
import com.jumbotail.shippingestimator.dto.ShippingCalculateResponse;
import com.jumbotail.shippingestimator.dto.ShippingChargeResponse;

/**
 * Service interface for shipping charge calculations.
 */
public interface ShippingChargeService {

    /**
     * Calculate shipping charge from a warehouse to a customer.
     *
     * @param warehouseId   the warehouse ID
     * @param customerId    the customer ID
     * @param productId     the product ID (for weight info)
     * @param deliverySpeed delivery speed: "standard" or "express"
     * @return the calculated shipping charge
     */
    ShippingChargeResponse getShippingCharge(Long warehouseId, Long customerId,
                                             Long productId, String deliverySpeed);

    /**
     * Calculate shipping charge end-to-end: find nearest warehouse, then calculate charge.
     *
     * @param request the shipping calculation request
     * @return the combined response with shipping charge and nearest warehouse info
     */
    ShippingCalculateResponse calculateShippingCharge(ShippingCalculateRequest request);
}
