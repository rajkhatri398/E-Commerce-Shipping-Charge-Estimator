package com.jumbotail.shippingestimator.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a Warehouse in the marketplace network.
 * Warehouses are distribution points located across the country.
 */
@Entity
@Table(name = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 500)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    /** Maximum storage capacity in cubic meters */
    @Column(name = "capacity_cbm")
    private Double capacityCbm;

    /** Whether the warehouse is currently active */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
