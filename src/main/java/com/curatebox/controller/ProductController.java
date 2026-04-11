package com.curatebox.controller;

import com.curatebox.dto.ProductDTO;
import com.curatebox.model.Product;
import com.curatebox.service.facade.InventoryFacade;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductController handles all product-related REST API endpoints
 * Implements MVC Controller pattern with clear separation of concerns
 * Uses InventoryFacade to coordinate complex inventory operations
 * 
 * DESIGN PATTERN: Facade Pattern
 * ==============================
 * The ProductController now depends on InventoryFacade rather than
 * coordinating between multiple services (ProductService, SupplierService, InventoryService).
 * This simplifies the controller logic and improves maintainability.
 * 
 * Key Design Principles:
 * 1. Single Responsibility Principle (SRP):
 *    - Responsible ONLY for HTTP request/response handling
 *    - Delegates all business logic to InventoryFacade
 * 
 * 2. Dependency Inversion Principle (DIP):
 *    - Depends on Facade abstraction, not concrete services
 *    - Not tightly coupled to internal implementation changes
 * 
 * API Endpoints:
 * - GET    /api/products              : List all products
 * - GET    /api/products/{id}         : Get product by ID
 * - POST   /api/products              : Create new product
 * - PUT    /api/products/{id}         : Update product
 * - DELETE /api/products/{id}         : Delete product
 * - GET    /api/products/low-stock    : List low stock products
 * - PUT    /api/products/{id}/stock   : Update stock quantity
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final InventoryFacade inventoryFacade;

    public ProductController(InventoryFacade inventoryFacade) {
        this.inventoryFacade = inventoryFacade;
    }

    /**
     * Retrieve all products
     * GET /api/products
     *
     * @return List of all products in system
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(inventoryFacade.getAllProductsWithStock());
    }

    /**
     * Retrieve a specific product by ID
     * GET /api/products/{id}
     *
     * @param id Product ID
     * @return Product if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inventoryFacade.getAllProductsWithStock().stream()
                    .filter(p -> p.getProductId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new product
     * POST /api/products
     * Uses Facade to coordinate ProductService, SupplierService, and InventoryService
     *
     * @param dto Product data transfer object containing product details and supplierId
     * @return Created product with 201 status, 400 if invalid
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO dto) {
        try {
            Product created = inventoryFacade.createProductWithInventory(
                    dto,
                    dto.getSupplierId(),
                    dto.getStockQuantity()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing product
     * PUT /api/products/{id}
     * Uses Facade to coordinate between ProductService and SupplierService
     *
     * @param id Product ID to update
     * @param dto Updated product data
     * @return Updated product, 400 if invalid, 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        try {
            Product existing = inventoryFacade.getAllProductsWithStock().stream()
                    .filter(p -> p.getProductId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
            existing.setProductName(dto.getProductName());
            existing.setDescription(dto.getDescription());
            existing.setCategory(dto.getCategory());
            return ResponseEntity.ok(inventoryFacade.updateProductWithSupplier(id, existing, dto.getSupplierId()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a product
     * DELETE /api/products/{id}
     *
     * @param id Product ID to delete
     * @return No content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        inventoryFacade.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve all low stock products
     * GET /api/products/low-stock
     * Products with stock <= 10 are considered low stock
     * Uses Facade to coordinate between InventoryService and ProductService
     *
     * @return List of low stock products
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        return ResponseEntity.ok(inventoryFacade.getLowStockProducts());
    }

    /**
     * Update product stock quantity
     * PUT /api/products/{id}/stock
     * 
     * Uses InventoryFacade to coordinate between multiple services:
     * - Gets the product from ProductService
     * - Updates stock through InventoryService
     * - Triggers notification callbacks for low stock alerts
     *
     * @param id Product ID to update
     * @param body Map containing 'quantity' key with delta value (positive or negative)
     * @return Updated product with new stock level
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Integer quantity = body.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().build();
            }
            // Facade coordinates the update and notifies observers
            Product updated = inventoryFacade.updateStockWithNotification(id, quantity);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
