package com.jumbotail.shippingestimator.dto;

import lombok.*;

/**
 * DTO representing warehouse geo-location.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseLocationDto {

    private Double lat;

    @com.fasterxml.jackson.annotation.JsonProperty("long")
    private Double lng;
}
