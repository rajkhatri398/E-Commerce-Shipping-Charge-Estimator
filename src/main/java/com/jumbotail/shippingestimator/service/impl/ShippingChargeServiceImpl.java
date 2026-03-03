package com.jumbotail.shippingestimator.service.impl;

import com.jumbotail.shippingestimator.dto.*;
import com.jumbotail.shippingestimator.exception.InvalidParameterException;
import com.jumbotail.shippingestimator.exception.ResourceNotFoundException;
import com.jumbotail.shippingestimator.factory.TransportModeFactory;
import com.jumbotail.shippingestimator.model.Customer;
import com.jumbotail.shippingestimator.model.Product;
import com.jumbotail.shippingestimator.model.Warehouse;
import com.jumbotail.shippingestimator.repository.CustomerRepository;
import com.jumbotail.shippingestimator.repository.ProductRepository;
import com.jumbotail.shippingestimator.repository.WarehouseRepository;
import com.jumbotail.shippingestimator.service.ShippingChargeService;
import com.jumbotail.shippingestimator.service.WarehouseService;
import com.jumbotail.shippingestimator.strategy.TransportStrategy;
import com.jumbotail.shippingestimator.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of ShippingChargeService.
 *
 * <p>Calculates shipping charges based on:
 * <ul>
 *   <li>Distance between warehouse and customer (Haversine formula)</li>
 *   <li>Transport mode determined by distance (Strategy Pattern via Factory)</li>
 *   <li>Product weight</li>
 *   <li>Delivery speed (standard or express)</li>
 * </ul>
 * </p>
 *
 * <p>Shipping Charge Formula:
 * <br><b>Standard:</b> 10 (base) + (rate × distance × weight)
 * <br><b>Express:</b>  10 (base) + (1.2 × weight) + (rate × distance × weight)
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingChargeServiceImpl implements ShippingChargeService {

    private static final double BASE_CHARGE = 10.0;
    private static final double EXPRESS_SURCHARGE_PER_KG = 1.2;

    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final TransportModeFactory transportModeFactory;
    private final WarehouseService warehouseService;

    /**
     * Calculate shipping charge from a warehouse to a customer for a product.
     *
     * <p>Steps:
     * 1. Validate warehouse, customer, and product exist
     * 2. Calculate distance using Haversine formula
     * 3. Select transport mode based on distance (Factory Pattern)
     * 4. Calculate transport charge using selected strategy (Strategy Pattern)
     * 5. Apply delivery speed surcharges
     * </p>
     */
    @Override
    public ShippingChargeResponse getShippingCharge(Long warehouseId, Long customerId,
                                                     Long productId, String deliverySpeed) {
        log.info("Calculating shipping charge: warehouseId={}, customerId={}, productId={}, speed={}",
                warehouseId, customerId, productId, deliverySpeed);

        // Validate delivery speed
        validateDeliverySpeed(deliverySpeed);

        // Fetch entities
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        // Calculate distance between warehouse and customer
        double distanceKm = DistanceCalculator.calculateDistance(
                warehouse.getLatitude(), warehouse.getLongitude(),
                customer.getLatitude(), customer.getLongitude()
        );
        log.info("Distance from warehouse to customer: {} km", distanceKm);

        // Calculate the total shipping charge
        double totalCharge = computeTotalCharge(distanceKm, product.getWeightKg(), deliverySpeed);

        return ShippingChargeResponse.builder()
                .shippingCharge(roundToTwoDecimals(totalCharge))
                .build();
    }

    /**
     * End-to-end shipping charge calculation:
     * 1. Find nearest warehouse to seller
     * 2. Calculate shipping charge from that warehouse to customer
     * 3. Return combined response
     */
    @Override
    public ShippingCalculateResponse calculateShippingCharge(ShippingCalculateRequest request) {
        log.info("Calculating full shipping charge for request: {}", request);

        // Validate delivery speed
        validateDeliverySpeed(request.getDeliverySpeed());

        // Step 1: Find nearest warehouse
        NearestWarehouseResponse nearestWarehouse = warehouseService.findNearestWarehouse(
                request.getSellerId(), request.getProductId());

        // Step 2: Calculate shipping charge from nearest warehouse to customer
        ShippingChargeResponse chargeResponse = getShippingCharge(
                nearestWarehouse.getWarehouseId(),
                request.getCustomerId(),
                request.getProductId(),
                request.getDeliverySpeed()
        );

        // Step 3: Build combined response
        return ShippingCalculateResponse.builder()
                .shippingCharge(chargeResponse.getShippingCharge())
                .nearestWarehouse(nearestWarehouse)
                .build();
    }

    /**
     * Compute the total shipping charge based on distance, weight, and delivery speed.
     *
     * @param distanceKm    distance in kilometers
     * @param weightKg      product weight in kilograms
     * @param deliverySpeed "standard" or "express"
     * @return total shipping charge in INR
     */
    private double computeTotalCharge(double distanceKm, double weightKg, String deliverySpeed) {
        // Use Factory Pattern to select the correct Transport Strategy
        TransportStrategy strategy = transportModeFactory.getStrategy(distanceKm);
        log.info("Selected transport mode: {} for distance {} km", strategy.getTransportModeName(), distanceKm);

        // Calculate the base transport charge using Strategy Pattern
        double transportCharge = strategy.calculateCharge(distanceKm, weightKg);
        log.info("Transport charge: {} INR", transportCharge);

        // Apply delivery speed logic
        double totalCharge;
        if ("express".equalsIgnoreCase(deliverySpeed)) {
            // Express: base charge + express surcharge + transport charge
            double expressSurcharge = EXPRESS_SURCHARGE_PER_KG * weightKg;
            totalCharge = BASE_CHARGE + expressSurcharge + transportCharge;
            log.info("Express delivery: base={} + expressSurcharge={} + transport={} = {}",
                    BASE_CHARGE, expressSurcharge, transportCharge, totalCharge);
        } else {
            // Standard: base charge + transport charge
            totalCharge = BASE_CHARGE + transportCharge;
            log.info("Standard delivery: base={} + transport={} = {}",
                    BASE_CHARGE, transportCharge, totalCharge);
        }

        return totalCharge;
    }

    /**
     * Validate that the delivery speed is either "standard" or "express".
     */
    private void validateDeliverySpeed(String deliverySpeed) {
        if (deliverySpeed == null || deliverySpeed.isBlank()) {
            throw new InvalidParameterException("deliverySpeed is required");
        }
        if (!"standard".equalsIgnoreCase(deliverySpeed) && !"express".equalsIgnoreCase(deliverySpeed)) {
            throw new InvalidParameterException(
                    "Invalid delivery speed: '" + deliverySpeed + "'. Must be 'standard' or 'express'");
        }
    }

    /**
     * Round a double value to two decimal places.
     */
    private double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
