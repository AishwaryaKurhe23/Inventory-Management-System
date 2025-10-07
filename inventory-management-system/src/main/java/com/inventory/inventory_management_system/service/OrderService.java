package com.inventory.inventory_management_system.service;

import com.inventory.inventory_management_system.model.Order;
import com.inventory.inventory_management_system.model.Product;
import com.inventory.inventory_management_system.repository.OrderRepository;
import com.inventory.inventory_management_system.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }
    public List<Order> getOrdersByProduct(Long productId) {
        return orderRepository.findByProductId(productId);
    }


    @Transactional
    public Order placeOrder(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("❌ Product not found with ID: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("❌ Insufficient stock for product: " + product.getName());
        }

        // Decrease stock and save product
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        // Create and save order
        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);

        return orderRepository.save(order);
    }
}
