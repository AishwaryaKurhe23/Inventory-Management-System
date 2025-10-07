package com.inventory.inventory_management_system.controller;

import com.inventory.inventory_management_system.model.Product;
import com.inventory.inventory_management_system.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {

    private final InventoryService inventoryService;

    public ProductController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ✅ Create new product
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product p) {
        return ResponseEntity.status(201).body(inventoryService.create(p));
    }

    // ✅ List all products
    @GetMapping
    public List<Product> list() {
        return inventoryService.list();
    }

    // ✅ Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Product p = inventoryService.get(id);
        return (p == null)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(p);
    }

    // ✅ Update existing product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return inventoryService.updateProduct(id, updatedProduct);
    }

    // ✅ Delete product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Increase stock
    @PostMapping("/{id}/increase")
    public ResponseEntity<?> increase(@PathVariable Long id, @RequestParam int amount) {
        return ResponseEntity.ok(inventoryService.increaseStock(id, amount));
    }

    // ✅ Decrease stock
    @PostMapping("/{id}/decrease")
    public ResponseEntity<?> decrease(@PathVariable Long id, @RequestParam int amount) {
        try {
            return ResponseEntity.ok(inventoryService.decreaseStock(id, amount));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // ✅ Get low stock products
    @GetMapping("/low-stock")
    public List<Product> lowStock() {
        return inventoryService.lowStock();
    }

    // ✅ Reset Database (Delete all products & reset auto-increment)
    @DeleteMapping("/reset")
    public ResponseEntity<?> resetDatabase() {
        return inventoryService.resetDatabase();
    }
 // ✅ Search products by name (case-insensitive)
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return inventoryService.searchProducts(keyword);
    }

}
