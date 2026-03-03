package com.jumbotail.shippingestimator.dto;

import lombok.*;

/**
 * DTO representing warehouse geo-location coordinates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseLocationDto {

    private Double lat;

    /** Using 'lng' in JSON output; mapped from longitude */
    @com.fasterxml.jackson.annotation.JsonProperty("long")
    private Double lng;
}
