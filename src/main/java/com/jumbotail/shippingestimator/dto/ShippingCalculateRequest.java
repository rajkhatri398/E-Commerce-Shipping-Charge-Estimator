package com.jumbotail.shippingestimator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request DTO for the POST /api/v1/shipping-charge/calculate endpoint.
 * Combines seller, customer, product, and delivery speed to compute shipping.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingCalculateRequest {

    @NotNull(message = "sellerId is required")
    private Long sellerId;

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "deliverySpeed is required")
    private String deliverySpeed;
}
