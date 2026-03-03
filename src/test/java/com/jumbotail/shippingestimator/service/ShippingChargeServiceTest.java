package com.jumbotail.shippingestimator.service;

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
import com.jumbotail.shippingestimator.service.impl.ShippingChargeServiceImpl;
import com.jumbotail.shippingestimator.strategy.AeroplaneStrategy;
import com.jumbotail.shippingestimator.strategy.MiniVanStrategy;
import com.jumbotail.shippingestimator.strategy.TruckStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingChargeServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransportModeFactory transportModeFactory;

    @Mock
    private WarehouseService warehouseService;

    @InjectMocks
    private ShippingChargeServiceImpl shippingChargeService;

    private Warehouse warehouse;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder()
                .id(1L)
                .name("BLR_Warehouse")
                .latitude(12.9716)
                .longitude(77.5946)
                .active(true)
                .build();

        // Customer nearby (~4.5 km from warehouse) -> MiniVan territory
        customer = Customer.builder()
                .id(1L)
                .name("Nearby Store")
                .latitude(12.9352)
                .longitude(77.6245)
                .phone("9847123456")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .sellerId(1L)
                .price(100.0)
                .weightKg(5.0)
                .build();
    }

    @Test
    @DisplayName("Standard delivery: should calculate base + transport charge")
    void testStandardDeliveryCharge() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(transportModeFactory.getStrategy(anyDouble())).thenReturn(new MiniVanStrategy());

        ShippingChargeResponse response = shippingChargeService.getShippingCharge(1L, 1L, 1L, "standard");

        assertNotNull(response);
        // The charge should be > 10 (base charge)
        assertTrue(response.getShippingCharge() > 10.0);
    }

    @Test
    @DisplayName("Express delivery: should include express surcharge")
    void testExpressDeliveryCharge() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(transportModeFactory.getStrategy(anyDouble())).thenReturn(new MiniVanStrategy());

        ShippingChargeResponse standardResp = shippingChargeService.getShippingCharge(1L, 1L, 1L, "standard");
        ShippingChargeResponse expressResp = shippingChargeService.getShippingCharge(1L, 1L, 1L, "express");

        // Express should be more expensive than standard
        assertTrue(expressResp.getShippingCharge() > standardResp.getShippingCharge(),
                "Express charge should be higher than standard");
    }

    @Test
    @DisplayName("Express surcharge should be 1.2 per kg extra")
    void testExpressSurchargeCalculation() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(transportModeFactory.getStrategy(anyDouble())).thenReturn(new MiniVanStrategy());

        ShippingChargeResponse standard = shippingChargeService.getShippingCharge(1L, 1L, 1L, "standard");
        ShippingChargeResponse express = shippingChargeService.getShippingCharge(1L, 1L, 1L, "express");

        double difference = express.getShippingCharge() - standard.getShippingCharge();
        // Express surcharge should be 1.2 * 5 = 6.0
        assertEquals(6.0, difference, 0.01);
    }

    @Test
    @DisplayName("Should throw InvalidParameterException for invalid delivery speed")
    void testInvalidDeliverySpeed() {
        assertThrows(InvalidParameterException.class,
                () -> shippingChargeService.getShippingCharge(1L, 1L, 1L, "overnight"));
    }

    @Test
    @DisplayName("Should throw InvalidParameterException for null delivery speed")
    void testNullDeliverySpeed() {
        assertThrows(InvalidParameterException.class,
                () -> shippingChargeService.getShippingCharge(1L, 1L, 1L, null));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid warehouse ID")
    void testInvalidWarehouseId() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shippingChargeService.getShippingCharge(999L, 1L, 1L, "standard"));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid customer ID")
    void testInvalidCustomerId() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shippingChargeService.getShippingCharge(1L, 999L, 1L, "standard"));
    }

    @Test
    @DisplayName("Calculate end-to-end shipping should combine nearest warehouse + charge")
    void testCalculateShippingCharge() {
        NearestWarehouseResponse nearestWarehouse = NearestWarehouseResponse.builder()
                .warehouseId(1L)
                .warehouseLocation(WarehouseLocationDto.builder()
                        .lat(12.9716)
                        .lng(77.5946)
                        .build())
                .build();

        when(warehouseService.findNearestWarehouse(1L, 1L)).thenReturn(nearestWarehouse);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(transportModeFactory.getStrategy(anyDouble())).thenReturn(new MiniVanStrategy());

        ShippingCalculateRequest request = ShippingCalculateRequest.builder()
                .sellerId(1L)
                .customerId(1L)
                .productId(1L)
                .deliverySpeed("standard")
                .build();

        ShippingCalculateResponse response = shippingChargeService.calculateShippingCharge(request);

        assertNotNull(response);
        assertNotNull(response.getNearestWarehouse());
        assertEquals(1L, response.getNearestWarehouse().getWarehouseId());
        assertTrue(response.getShippingCharge() > 0);
    }

    @Test
    @DisplayName("Truck strategy should be used for medium distance")
    void testTruckStrategySelection() {
        Customer farCustomer = Customer.builder()
                .id(2L)
                .name("Far Store")
                .latitude(17.3850)
                .longitude(78.4867)
                .phone("9847123456")
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(farCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(transportModeFactory.getStrategy(anyDouble())).thenReturn(new TruckStrategy());

        ShippingChargeResponse response = shippingChargeService.getShippingCharge(1L, 2L, 1L, "standard");

        assertNotNull(response);
        verify(transportModeFactory).getStrategy(anyDouble());
    }

    @Test
    @DisplayName("Aeroplane strategy should be used for long distance")
    void testAeroplaneStrategySelection() {
        Customer veryFarCustomer = Customer.builder()
                .id(3L)
                .name("Very Far Store")
                .latitude(28.7041)
                .longitude(77.1025)
                .phone("9847123456")
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(3L)).thenReturn(Optional.of(veryFarCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(transportModeFactory.getStrategy(anyDouble())).thenReturn(new AeroplaneStrategy());

        ShippingChargeResponse response = shippingChargeService.getShippingCharge(1L, 3L, 1L, "standard");

        assertNotNull(response);
        verify(transportModeFactory).getStrategy(anyDouble());
    }
}
