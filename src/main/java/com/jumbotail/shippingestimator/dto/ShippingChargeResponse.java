package com.jumbotail.shippingestimator.dto;

import lombok.*;

/**
 * Response DTO for the shipping charge GET API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingChargeResponse {

    private Double shippingCharge;
}
