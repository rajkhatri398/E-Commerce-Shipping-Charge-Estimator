package com.jumbotail.shippingestimator.service;

import com.jumbotail.shippingestimator.dto.NearestWarehouseResponse;
import com.jumbotail.shippingestimator.exception.NoWarehouseAvailableException;
import com.jumbotail.shippingestimator.exception.ResourceNotFoundException;
import com.jumbotail.shippingestimator.model.Product;
import com.jumbotail.shippingestimator.model.Seller;
import com.jumbotail.shippingestimator.model.Warehouse;
import com.jumbotail.shippingestimator.repository.ProductRepository;
import com.jumbotail.shippingestimator.repository.SellerRepository;
import com.jumbotail.shippingestimator.repository.WarehouseRepository;
import com.jumbotail.shippingestimator.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Seller seller;
    private Product product;
    private Warehouse blrWarehouse;
    private Warehouse mumbWarehouse;

    @BeforeEach
    void setUp() {
        seller = Seller.builder()
                .id(1L)
                .name("Test Seller")
                .latitude(12.9352)
                .longitude(77.6245)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .sellerId(1L)
                .price(100.0)
                .weightKg(5.0)
                .build();

        blrWarehouse = Warehouse.builder()
                .id(1L)
                .name("BLR_Warehouse")
                .latitude(12.9716)
                .longitude(77.5946)
                .active(true)
                .build();

        mumbWarehouse = Warehouse.builder()
                .id(2L)
                .name("MUMB_Warehouse")
                .latitude(19.0760)
                .longitude(72.8777)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should return nearest warehouse (BLR is closer to seller in Bangalore)")
    void testFindNearestWarehouse() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findByActiveTrue()).thenReturn(List.of(blrWarehouse, mumbWarehouse));

        NearestWarehouseResponse response = warehouseService.findNearestWarehouse(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getWarehouseId());
        assertNotNull(response.getWarehouseLocation());
        assertEquals(12.9716, response.getWarehouseLocation().getLat());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid seller ID")
    void testInvalidSellerId() {
        when(sellerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.findNearestWarehouse(999L, 1L));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid product ID")
    void testInvalidProductId() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.findNearestWarehouse(1L, 999L));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product doesn't belong to seller")
    void testProductNotBelongingToSeller() {
        Product otherProduct = Product.builder()
                .id(2L)
                .name("Other Product")
                .sellerId(99L)
                .price(50.0)
                .weightKg(1.0)
                .build();

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(2L)).thenReturn(Optional.of(otherProduct));

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.findNearestWarehouse(1L, 2L));
    }

    @Test
    @DisplayName("Should throw NoWarehouseAvailableException when no active warehouses")
    void testNoActiveWarehouses() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        assertThrows(NoWarehouseAvailableException.class,
                () -> warehouseService.findNearestWarehouse(1L, 1L));
    }

    @Test
    @DisplayName("Should return the only warehouse when there's just one")
    void testSingleWarehouse() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findByActiveTrue()).thenReturn(List.of(mumbWarehouse));

        NearestWarehouseResponse response = warehouseService.findNearestWarehouse(1L, 1L);

        assertEquals(2L, response.getWarehouseId());
    }
}
