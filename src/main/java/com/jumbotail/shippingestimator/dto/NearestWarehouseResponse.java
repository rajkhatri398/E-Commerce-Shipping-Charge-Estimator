package com.jumbotail.shippingestimator.dto;

import lombok.*;

/**
 * Response DTO for the nearest warehouse API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearestWarehouseResponse {

    private Long warehouseId;

    private WarehouseLocationDto warehouseLocation;
}
