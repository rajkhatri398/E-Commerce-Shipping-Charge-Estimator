package com.jumbotail.shippingestimator.dto;

import lombok.*;

/**
 * Response DTO for the nearest warehouse API.
 * Contains warehouse ID and its geo-location.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearestWarehouseResponse {

    private Long warehouseId;

    private WarehouseLocationDto warehouseLocation;
}
