package com.inventory.inventory_management_system.repository;


import com.inventory.inventory_management_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < p.lowStockThreshold")
    List<Product> findLowStockProducts();
}
