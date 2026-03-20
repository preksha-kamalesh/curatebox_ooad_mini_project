package com.curatebox.controller;

import com.curatebox.dto.ProductDTO;
import com.curatebox.model.Product;
import com.curatebox.service.InventoryService;
import com.curatebox.service.ProductService;
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
 * 
 * Key Design Principles:
 * 1. Single Responsibility Principle (SRP):
 *    - Responsible ONLY for HTTP request/response handling
 *    - Delegates business logic to ProductService
 *    - Delegates inventory operations to InventoryService
 * 
 * 2. Dependency Inversion Principle (DIP):
 *    - Depends on service interfaces/abstractions
 *    - Not tightly coupled to implementation details
 * 
 * API Endpoints:
 * - GET    /api/products              : List all products
 * - GET    /api/products/{id}         : Get product by ID
 * - POST   /api/products              : Create new product
 * - PUT    /api/products/{id}         : Update product
 * - DELETE /api/products/{id}         : Delete product
 * - GET    /api/products/low-stock    : List low stock products
 * - PUT    /api/products/{id}/stock   : Update stock quantity (triggers Observer pattern)
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final InventoryService inventoryService;

    public ProductController(ProductService productService, InventoryService inventoryService) {
        this.productService = productService;
        this.inventoryService = inventoryService;
    }

    /**
     * Retrieve all products
     * GET /api/products
     *
     * @return List of all products in system
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
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
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new product
     * POST /api/products
     *
     * @param dto Product data transfer object containing product details
     * @return Created product with 201 status, 400 if invalid
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing product
     * PUT /api/products/{id}
     *
     * @param id Product ID to update
     * @param dto Updated product data
     * @return Updated product, 400 if invalid, 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, dto));
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
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve all low stock products
     * GET /api/products/low-stock
     * Products with stock <= 10 are considered low stock
     *
     * @return List of low stock products
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }

    /**
     * Update product stock quantity
     * PUT /api/products/{id}/stock
     * 
     * Triggers the Observer Pattern:
     * - Quantity delta is applied to product's current stock
     * - If resulting stock <= 10, all registered observers are notified
     * - LowStockAlertObserver receives notification and logs alert
     *
     * @param id Product ID to update
     * @param body Map containing 'quantity' key with delta value (positive or negative)
     * @return Updated product with new stock level
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Product product = productService.getProductById(id);
            Integer quantity = body.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().build();
            }
            // This call triggers the Observer Pattern notification
            inventoryService.updateStock(product, quantity);
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
