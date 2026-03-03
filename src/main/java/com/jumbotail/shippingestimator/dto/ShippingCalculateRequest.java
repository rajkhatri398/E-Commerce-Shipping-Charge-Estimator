package com.jumbotail.shippingestimator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request DTO for the end-to-end shipping charge calculation API.
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
