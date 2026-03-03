package com.jumbotail.shippingestimator.dto;

import lombok.*;

/**
 * Response DTO for the end-to-end shipping charge calculation API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingCalculateResponse {

    private Double shippingCharge;

    private NearestWarehouseResponse nearestWarehouse;
}
