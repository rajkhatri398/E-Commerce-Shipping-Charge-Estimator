package com.jumbotail.shippingestimator.repository;

import com.jumbotail.shippingestimator.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Warehouse entity.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    /**
     * Find all active warehouses.
     *
     * @return list of active warehouses
     */
    List<Warehouse> findByActiveTrue();
}
