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
 * Shipping charge calculation service.
 *
 * <p>Formula:
 * <br>Standard: BASE_CHARGE + (rate x distance x weight)
 * <br>Express:  BASE_CHARGE + (EXPRESS_SURCHARGE x weight) + (rate x distance x weight)
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

    @Override
    public ShippingChargeResponse getShippingCharge(Long warehouseId, Long customerId,
                                                     Long productId, String deliverySpeed) {
        log.info("Calculating shipping charge: warehouseId={}, customerId={}, productId={}, speed={}",
                warehouseId, customerId, productId, deliverySpeed);

        validateDeliverySpeed(deliverySpeed);

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        double distanceKm = DistanceCalculator.calculateDistance(
                warehouse.getLatitude(), warehouse.getLongitude(),
                customer.getLatitude(), customer.getLongitude()
        );
        log.info("Distance from warehouse to customer: {} km", distanceKm);

        double totalCharge = computeTotalCharge(distanceKm, product.getWeightKg(), deliverySpeed);

        return ShippingChargeResponse.builder()
                .shippingCharge(roundToTwoDecimals(totalCharge))
                .build();
    }

    /**
     * End-to-end: find nearest warehouse, then calculate shipping charge.
     */
    @Override
    public ShippingCalculateResponse calculateShippingCharge(ShippingCalculateRequest request) {
        log.info("Calculating full shipping charge for request: {}", request);

        validateDeliverySpeed(request.getDeliverySpeed());

        NearestWarehouseResponse nearestWarehouse = warehouseService.findNearestWarehouse(
                request.getSellerId(), request.getProductId());

        ShippingChargeResponse chargeResponse = getShippingCharge(
                nearestWarehouse.getWarehouseId(),
                request.getCustomerId(),
                request.getProductId(),
                request.getDeliverySpeed()
        );

        return ShippingCalculateResponse.builder()
                .shippingCharge(chargeResponse.getShippingCharge())
                .nearestWarehouse(nearestWarehouse)
                .build();
    }

    private double computeTotalCharge(double distanceKm, double weightKg, String deliverySpeed) {
        TransportStrategy strategy = transportModeFactory.getStrategy(distanceKm);
        log.info("Selected transport mode: {} for distance {} km", strategy.getTransportModeName(), distanceKm);

        double transportCharge = strategy.calculateCharge(distanceKm, weightKg);
        log.info("Transport charge: {} INR", transportCharge);

        double totalCharge;
        if ("express".equalsIgnoreCase(deliverySpeed)) {
            double expressSurcharge = EXPRESS_SURCHARGE_PER_KG * weightKg;
            totalCharge = BASE_CHARGE + expressSurcharge + transportCharge;
            log.info("Express delivery: base={} + expressSurcharge={} + transport={} = {}",
                    BASE_CHARGE, expressSurcharge, transportCharge, totalCharge);
        } else {
            totalCharge = BASE_CHARGE + transportCharge;
            log.info("Standard delivery: base={} + transport={} = {}",
                    BASE_CHARGE, transportCharge, totalCharge);
        }

        return totalCharge;
    }

    /**
     * Validate delivery speed is either "standard" or "express".
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

    private double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
