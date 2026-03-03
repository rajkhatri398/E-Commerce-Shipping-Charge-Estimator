package com.jumbotail.shippingestimator.dto;

import lombok.*;

/**
 * Response DTO for the combined shipping charge calculation API.
 * Returns both the shipping charge and the nearest warehouse details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingCalculateResponse {

    private Double shippingCharge;

    private NearestWarehouseResponse nearestWarehouse;
}
