package com.jumbotail.shippingestimator.repository;

import com.jumbotail.shippingestimator.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for Seller entity.
 */
@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
}
