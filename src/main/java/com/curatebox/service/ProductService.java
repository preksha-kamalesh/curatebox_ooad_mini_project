package com.curatebox.service;

import com.curatebox.dto.ProductDTO;
import com.curatebox.model.Product;
import com.curatebox.model.Supplier;
import com.curatebox.repository.ProductRepository;
import com.curatebox.repository.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductService handles business logic for product management
 * Implements core design principles and patterns:
 * 
 * Design Principles Applied:
 * 1. Single Responsibility Principle (SRP):
 *    - Responsible ONLY for product business logic
 *    - Does not handle HTTP requests or persistence details
 * 
 * 2. Dependency Inversion Principle (DIP):
 *    - Depends on Repository abstractions (ProductRepository, SupplierRepository)
 *    - Not directly coupled to database implementation
 * 
 * 3. Open/Closed Principle (OCP):
 *    - Open for extension through new product service methods
 *    - Closed for modification through stable interface
 * 
 * The service integrates with InventoryService for inventory tracking
 * This ensures separation of concerns: ProductService handles CRUD,
 * while InventoryService handles stock level notifications (Observer Pattern)
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    /**
     * Retrieve all products from the system
     *
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieve a specific product by its ID
     *
     * @param id Product ID
     * @return Product object if found
     * @throws IllegalArgumentException if product not found
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    /**
     * Create a new product with optional supplier association
     * If supplier ID is provided, validates that the supplier exists
     * If supplier ID is null/empty, creates product without a supplier
     *
     * @param dto Product data transfer object
     * @return Created product entity
     * @throws IllegalArgumentException if supplier ID is provided but not found
     */
    @Transactional
    public Product createProduct(ProductDTO dto) {
        Product p = new Product();
        p.setProductName(dto.getProductName());
        p.setDescription(dto.getDescription());
        p.setCategory(dto.getCategory());
        p.setStockQuantity(dto.getStockQuantity());
        
        // Supplier is optional - only set if supplierId is provided
        if (dto.getSupplierId() != null && dto.getSupplierId() > 0) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + dto.getSupplierId()));
            p.setSupplier(supplier);
        }
        
        return productRepository.save(p);
    }

    /**
     * Update an existing product
     *
     * @param id Product ID to update
     * @param dto Updated product data
     * @return Updated product entity
     * @throws IllegalArgumentException if product or supplier not found
     */
    @Transactional
    public Product updateProduct(Long id, ProductDTO dto) {
        Product p = getProductById(id);
        p.setProductName(dto.getProductName());
        p.setDescription(dto.getDescription());
        p.setCategory(dto.getCategory());
        p.setStockQuantity(dto.getStockQuantity());
        
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + dto.getSupplierId()));
            p.setSupplier(supplier);
        }
        return productRepository.save(p);
    }

    /**
     * Delete a product by ID
     *
     * @param id Product ID to delete
     */
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Retrieve all products with low stock
     * Used by InventoryService to identify products needing alerts
     *
     * @return List of products with stock quantity <= 10
     */
    public List<Product> getLowStockProducts() {
        return productRepository.findByStockQuantityLessThanEqual(10);
    }

    /**
     * Retrieve products by category
     *
     * @param category Product category
     * @return List of products in the specified category
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
