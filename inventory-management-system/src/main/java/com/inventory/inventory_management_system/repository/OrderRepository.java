package com.inventory.inventory_management_system.repository;


import com.inventory.inventory_management_system.model.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> { 

    List<Order> findByProductId(Long productId);
}