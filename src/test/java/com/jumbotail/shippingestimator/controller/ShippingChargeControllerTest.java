package com.jumbotail.shippingestimator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbotail.shippingestimator.dto.*;
import com.jumbotail.shippingestimator.exception.InvalidParameterException;
import com.jumbotail.shippingestimator.exception.ResourceNotFoundException;
import com.jumbotail.shippingestimator.service.ShippingChargeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShippingChargeController.class)
class ShippingChargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShippingChargeService shippingChargeService;

    @Test
    @DisplayName("GET /api/v1/shipping-charge - should return shipping charge")
    void testGetShippingCharge() throws Exception {
        ShippingChargeResponse response = ShippingChargeResponse.builder()
                .shippingCharge(150.00)
                .build();

        when(shippingChargeService.getShippingCharge(1L, 1L, 1L, "standard")).thenReturn(response);

        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "1")
                        .param("customerId", "1")
                        .param("productId", "1")
                        .param("deliverySpeed", "standard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").value(150.00));
    }

    @Test
    @DisplayName("GET /api/v1/shipping-charge - missing params should return 400")
    void testMissingParameters() throws Exception {
        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/shipping-charge - invalid delivery speed should return 400")
    void testInvalidDeliverySpeed() throws Exception {
        when(shippingChargeService.getShippingCharge(1L, 1L, 1L, "overnight"))
                .thenThrow(new InvalidParameterException("Invalid delivery speed: 'overnight'. Must be 'standard' or 'express'"));

        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "1")
                        .param("customerId", "1")
                        .param("productId", "1")
                        .param("deliverySpeed", "overnight"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /api/v1/shipping-charge - invalid warehouse should return 404")
    void testInvalidWarehouse() throws Exception {
        when(shippingChargeService.getShippingCharge(999L, 1L, 1L, "standard"))
                .thenThrow(new ResourceNotFoundException("Warehouse", 999L));

        mockMvc.perform(get("/api/v1/shipping-charge")
                        .param("warehouseId", "999")
                        .param("customerId", "1")
                        .param("productId", "1")
                        .param("deliverySpeed", "standard"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/shipping-charge/calculate - should return combined response")
    void testCalculateShippingCharge() throws Exception {
        ShippingCalculateResponse response = ShippingCalculateResponse.builder()
                .shippingCharge(180.00)
                .nearestWarehouse(NearestWarehouseResponse.builder()
                        .warehouseId(1L)
                        .warehouseLocation(WarehouseLocationDto.builder()
                                .lat(12.9716)
                                .lng(77.5946)
                                .build())
                        .build())
                .build();

        when(shippingChargeService.calculateShippingCharge(any(ShippingCalculateRequest.class)))
                .thenReturn(response);

        ShippingCalculateRequest request = ShippingCalculateRequest.builder()
                .sellerId(1L)
                .customerId(1L)
                .productId(1L)
                .deliverySpeed("express")
                .build();

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").value(180.00))
                .andExpect(jsonPath("$.nearestWarehouse.warehouseId").value(1))
                .andExpect(jsonPath("$.nearestWarehouse.warehouseLocation.lat").value(12.9716));
    }

    @Test
    @DisplayName("POST /api/v1/shipping-charge/calculate - missing fields should return 400")
    void testCalculateMissingFields() throws Exception {
        // Missing required fields
        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/shipping-charge/calculate - partial fields should return 400")
    void testCalculatePartialFields() throws Exception {
        ShippingCalculateRequest request = ShippingCalculateRequest.builder()
                .sellerId(1L)
                .build();

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
