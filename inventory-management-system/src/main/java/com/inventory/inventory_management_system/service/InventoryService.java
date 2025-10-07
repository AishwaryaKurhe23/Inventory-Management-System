package com.inventory.inventory_management_system.service;

import com.inventory.inventory_management_system.model.Product;
import com.inventory.inventory_management_system.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ✅ Create Product
    public Product create(Product p) {
        if (p.getStockQuantity() < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }
        return productRepository.save(p);
    }

    // ✅ List All
    public List<Product> list() {
        return productRepository.findAll();
    }

    // ✅ Get by ID
    public Product get(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // ✅ Delete
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    // ✅ Update Product (Partial + Safe)
    @Transactional
    public ResponseEntity<?> updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingOpt = productRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Product not found with ID: " + id);
        }

        Product existing = existingOpt.get();

        // Only update non-null / non-empty fields
        if (updatedProduct.getName() != null && !updatedProduct.getName().trim().isEmpty()) {
            existing.setName(updatedProduct.getName());
        }
        if (updatedProduct.getDescription() != null && !updatedProduct.getDescription().trim().isEmpty()) {
            existing.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getStockQuantity() != null && updatedProduct.getStockQuantity() >= 0) {
            existing.setStockQuantity(updatedProduct.getStockQuantity());
        }
        if (updatedProduct.getLowStockThreshold() != null && updatedProduct.getLowStockThreshold() >= 0) {
            existing.setLowStockThreshold(updatedProduct.getLowStockThreshold());
        }

        // ✅ Update timestamp automatically via @PreUpdate
        Product saved = productRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    // ✅ Increase Stock
    public Product increaseStock(Long id, int amount) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        p.setStockQuantity(p.getStockQuantity() + amount);
        return productRepository.save(p);
    }

    // ✅ Decrease Stock
    public Product decreaseStock(Long id, int amount) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (p.getStockQuantity() < amount) {
            throw new RuntimeException("Insufficient stock available!");
        }
        p.setStockQuantity(p.getStockQuantity() - amount);
        return productRepository.save(p);
    }

    // ✅ Low Stock Products
    public List<Product> lowStock() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getStockQuantity() <= p.getLowStockThreshold())
                .toList();
    }

    // ✅ Reset Database (Delete all and reset ID)
    @Transactional
    public ResponseEntity<?> resetDatabase() {
        try {
            productRepository.deleteAll();
            entityManager.createNativeQuery("ALTER TABLE products AUTO_INCREMENT = 1").executeUpdate();
            return ResponseEntity.ok("✅ All products deleted and ID reset!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Failed to reset database: " + e.getMessage());
        }
    }

    // ✅ Search Products
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findAll().stream()
                .filter(p -> p.getName() != null &&
                        p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}
