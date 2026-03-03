package com.jumbotail.shippingestimator.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a Product listed by a Seller.
 * Contains weight and dimensions needed for shipping charge calculation.
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

    /** Foreign key to the seller who owns this product */
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    /** Selling price in INR */
    @Column(nullable = false)
    private Double price;

    /** Weight of the product in kilograms */
    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    /** Length of the product in centimeters */
    @Column(name = "length_cm")
    private Double lengthCm;

    /** Width of the product in centimeters */
    @Column(name = "width_cm")
    private Double widthCm;

    /** Height of the product in centimeters */
    @Column(name = "height_cm")
    private Double heightCm;

    @Column(length = 100)
    private String category;

    @Column(length = 500)
    private String description;
}
