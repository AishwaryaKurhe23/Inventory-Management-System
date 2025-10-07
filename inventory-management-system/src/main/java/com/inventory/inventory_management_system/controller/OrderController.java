package com.inventory.inventory_management_system.controller;

import com.inventory.inventory_management_system.dto.OrderRequest;
import com.inventory.inventory_management_system.model.Order;
import com.inventory.inventory_management_system.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ Get all orders
    @GetMapping
    public List<Order> list() {
        return orderService.list();
    }

    // ✅ Place a new order (reduce stock)
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest req) {
        try {
            Order newOrder = orderService.placeOrder(req.getProductId(), req.getQuantity());
            return ResponseEntity.status(201).body(newOrder);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
 // ✅ Get Orders by Product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getOrdersByProduct(@PathVariable Long productId) {
        List<Order> orders = orderService.getOrdersByProduct(productId);
        if (orders.isEmpty()) {
            return ResponseEntity.ok("No orders found for Product ID: " + productId);
        }
        return ResponseEntity.ok(orders);
    }

}
