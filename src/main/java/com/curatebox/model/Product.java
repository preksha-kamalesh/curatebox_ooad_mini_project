package com.curatebox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Product model represents a physical product in the CurateBox system
 * Products are created by suppliers and included in monthly boxes
 * 
 * Key Responsibilities:
 * - Store product information (name, description, category)
 * - Track stock quantity and inventory status
 * - Maintain relationship with Supplier (many-to-one)
 * - Maintain relationship with BoxContent (one-to-many)
 * 
 * Design Principles Applied:
 * - Single Responsibility: Represents only product domain entity
 * - Maintains clear relationships without over-coupling
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;
    private String description;
    private String category;
    private int stockQuantity;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<BoxContent> boxContents = new ArrayList<>();

    /**
     * Update product stock quantity
     * Prevents stock from going negative
     * 
     * @param quantity Quantity delta (positive for restock, negative for usage)
     */
    public void updateStock(int quantity) {
        this.stockQuantity += quantity;
        if (this.stockQuantity < 0) {
            this.stockQuantity = 0;
        }
    }

    /**
     * Check if product is in stock
     * 
     * @return true if stock quantity > 0
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Check if product stock is low
     * Low stock is defined as: 0 < quantity <= 10
     * Used by InventoryService to trigger Observer pattern notifications
     * 
     * @return true if stock is between 1-10 units
     */
    public boolean isLowStock() {
        return stockQuantity > 0 && stockQuantity <= 10;
    }

    // Getters and Setters
    
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<BoxContent> getBoxContents() {
        return boxContents;
    }

    public void setBoxContents(List<BoxContent> boxContents) {
        this.boxContents = boxContents;
    }
}
