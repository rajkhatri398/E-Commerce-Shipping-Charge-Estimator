package com.jumbotail.shippingestimator.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a Product listed by a Seller.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Double price;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "length_cm")
    private Double lengthCm;

    @Column(name = "width_cm")
    private Double widthCm;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(length = 100)
    private String category;

    @Column(length = 500)
    private String description;
}
