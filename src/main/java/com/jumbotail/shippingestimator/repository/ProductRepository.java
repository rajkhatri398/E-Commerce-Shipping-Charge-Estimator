package com.jumbotail.shippingestimator.repository;

import com.jumbotail.shippingestimator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products belonging to a specific seller.
     *
     * @param sellerId the seller's ID
     * @return list of products for the seller
     */
    List<Product> findBySellerId(Long sellerId);
}
