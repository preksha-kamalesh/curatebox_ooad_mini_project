package com.curatebox.service;

import com.curatebox.dto.ProductDTO;
import com.curatebox.dto.SupplierDTO;
import com.curatebox.model.Product;
import com.curatebox.model.Supplier;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InventoryFacade provides a unified, simplified interface for the complex
 * inventory management system by coordinating multiple internal services.
 * 
 * DESIGN PATTERN: Facade Pattern (Structural Pattern)
 * ==================================================
 * Purpose: Provide a unified interface to a set of interfaces in a subsystem.
 *          Facade defines a higher-level interface that makes the subsystem easier to use.
 * 
 * Problem Solved:
 * - ProductController had to coordinate between ProductService, SupplierService, and InventoryService
 * - This coupling made the code complex and hard to maintain
 * - Changes to internal services required changes to all clients
 * 
 * Solution: Single Facade entry point for all inventory operations
 * 
 * Participants:
 * - **Facade** (InventoryFacade): This class - provides simplified interface
 * - **Subsystems**:
 *   - ProductService: Handles product CRUD operations
 *   - SupplierService: Handles supplier CRUD operations
 *   - InventoryService: Handles stock management and low stock checks
 * - **Clients**: ProductController, InventoryViewController - use only the Facade
 * 
 * Benefits:
 * 1. **Simplified Interface**: Clients use simple methods instead of coordinating multiple services
 * 2. **Loose Coupling**: Clients depend only on Facade, not internal services
 * 3. **Easy Refactoring**: Internal services can change without affecting clients
 * 4. **Centralized Logic**: Complex multi-service operations are coordinated in one place
 * 
 * Design Principle Alignment:
 * - Single Responsibility Principle: Facade coordinates between services
 * - Open/Closed Principle: New facades can be added without modifying services
 * - Dependency Inversion: Controllers depend on Facade abstraction, not concrete services
 */
@Service
public class InventoryFacade {

    private final ProductService productService;
    private final SupplierService supplierService;
    private final InventoryService inventoryService;

    public InventoryFacade(ProductService productService,
                          SupplierService supplierService,
                          InventoryService inventoryService) {
        this.productService = productService;
        this.supplierService = supplierService;
        this.inventoryService = inventoryService;
    }

    /**
     * Facade Method: Create a product with inventory tracking in one operation
     * 
     * This method encapsulates the complexity of:
     * 1. Creating the product
     * 2. Associating with a supplier (if provided)
     * 3. Initializing inventory with stock quantity
     * 
     * Clients only call this single method instead of orchestrating three different services.
     *
     * @param dto Product data transfer object
     * @param supplierId Optional supplier ID
     * @param initialStock Initial stock quantity to set
     * @return Created product with inventory initialized
     */
    @Transactional
    public Product createProductWithInventory(ProductDTO dto, Long supplierId, int initialStock) {
        // Subsystem 1: Create product using ProductService
        Product product = productService.createProduct(dto);
        
        // Subsystem 2: Assign supplier if provided using SupplierService
        if (supplierId != null) {
            Supplier supplier = supplierService.getSupplierById(supplierId);
            product.setSupplier(supplier);
            productService.updateProduct(product.getProductId(), product, supplierId);
        }
        
        // Subsystem 3: Initialize stock and register for inventory notifications using InventoryService
        product.setStockQuantity(initialStock);
        productService.updateProduct(product.getProductId(), product, supplierId);
        
        return product;
    }

    /**
     * Facade Method: Update product stock and handle low stock checks
     * 
     * Coordinates with InventoryService to:
     * 1. Update the stock quantity
     * 2. Check if stock falls below threshold for alerts
     *
     * @param productId Product ID to update
     * @param quantity Quantity delta (positive for restock, negative for usage)
     * @return Updated product
     */
    @Transactional
    public Product updateStockWithNotification(Long productId, int quantity) {
        Product product = productService.getProductById(productId);
        inventoryService.updateStock(product, quantity);
        
        // Check for low stock condition (can be used for logging, alerts, etc.)
        if (inventoryService.isLowStock(product)) {
            System.out.println("[LOW STOCK ALERT] Product: " + product.getProductName() + 
                             " | Stock: " + product.getStockQuantity());
        }
        
        return productService.getProductById(productId);
    }

    /**
     * Facade Method: Retrieve all products with their current stock status
     * 
     * Simple wrapper around ProductService for consistency in Facade interface
     *
     * @return List of all products
     */
    public List<Product> getAllProductsWithStock() {
        return productService.getAllProducts();
    }

    /**
     * Facade Method: Get products that are currently low in stock
     * 
     * Coordinates with InventoryService to identify products below threshold
     * without requiring clients to understand the low stock logic
     *
     * @return List of products with stock <= LOW_STOCK_THRESHOLD
     */
    public List<Product> getLowStockProducts() {
        List<Product> allProducts = productService.getAllProducts();
        int threshold = inventoryService.getLowStockThreshold();
        return allProducts.stream()
                .filter(p -> p.getStockQuantity() <= threshold)
                .toList();
    }

    /**
     * Facade Method: Create a new supplier
     * 
     * Simple wrapper for consistency in Facade interface
     *
     * @param dto Supplier data transfer object
     * @return Created supplier
     */
    @Transactional
    public Supplier createSupplier(SupplierDTO dto) {
        return supplierService.createSupplier(dto);
    }

    /**
     * Facade Method: Get all suppliers
     *
     * @return List of all suppliers
     */
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    /**
     * Facade Method: Update product with new supplier assignment
     * 
     * Coordinates between ProductService and SupplierService
     *
     * @param productId Product ID
     * @param product Updated product data
     * @param supplierId New supplier ID
     * @return Updated product
     */
    @Transactional
    public Product updateProductWithSupplier(Long productId, Product product, Long supplierId) {
        if (supplierId != null) {
            Supplier supplier = supplierService.getSupplierById(supplierId);
            product.setSupplier(supplier);
        }
        return productService.updateProduct(productId, product, supplierId);
    }

    /**
     * Facade Method: Delete a product and its associated inventory tracking
     *
     * @param productId Product ID to delete
     */
    @Transactional
    public void deleteProduct(Long productId) {
        productService.deleteProduct(productId);
    }
}
