package com.jumbotail.shippingestimator.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a Seller on the marketplace.
 */
@Entity
@Table(name = "sellers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seller {

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

    @Column(length = 15)
    private String phone;

    @Column(length = 100)
    private String email;
}
