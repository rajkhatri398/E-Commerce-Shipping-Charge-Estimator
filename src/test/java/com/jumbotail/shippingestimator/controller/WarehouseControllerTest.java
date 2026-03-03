package com.jumbotail.shippingestimator.controller;

import com.jumbotail.shippingestimator.dto.NearestWarehouseResponse;
import com.jumbotail.shippingestimator.dto.WarehouseLocationDto;
import com.jumbotail.shippingestimator.exception.ResourceNotFoundException;
import com.jumbotail.shippingestimator.service.WarehouseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for WarehouseController using MockMvc.
 */
@WebMvcTest(WarehouseController.class)
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WarehouseService warehouseService;

    @Test
    @DisplayName("GET /api/v1/warehouse/nearest - should return nearest warehouse")
    void testGetNearestWarehouse() throws Exception {
        NearestWarehouseResponse response = NearestWarehouseResponse.builder()
                .warehouseId(1L)
                .warehouseLocation(WarehouseLocationDto.builder()
                        .lat(12.9716)
                        .lng(77.5946)
                        .build())
                .build();

        when(warehouseService.findNearestWarehouse(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("sellerId", "1")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warehouseId").value(1))
                .andExpect(jsonPath("$.warehouseLocation.lat").value(12.9716))
                .andExpect(jsonPath("$.warehouseLocation.long").value(77.5946));
    }

    @Test
    @DisplayName("GET /api/v1/warehouse/nearest - missing sellerId should return 400")
    void testMissingSellerId() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("productId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/warehouse/nearest - missing productId should return 400")
    void testMissingProductId() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("sellerId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/warehouse/nearest - invalid seller should return 404")
    void testInvalidSeller() throws Exception {
        when(warehouseService.findNearestWarehouse(999L, 1L))
                .thenThrow(new ResourceNotFoundException("Seller", 999L));

        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("sellerId", "999")
                        .param("productId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seller not found with id: 999"));
    }
}
