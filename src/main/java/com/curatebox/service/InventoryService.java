package com.curatebox.service;

import com.curatebox.model.Product;
import com.curatebox.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InventoryService manages inventory and stock operations
 * 
 * SUBSYSTEM COMPONENT OF FACADE PATTERN
 * ====================================
 * This service is one of the hidden subsystems coordinated by InventoryFacade.
 * It provides core inventory management functionality that the Facade simplifies.
 * 
 * Responsibilities:
 * - Update product stock quantities
 * - Check for low stock conditions
 * - Retrieve inventory information
 * 
 * **NEVER accessed directly by clients - always through InventoryFacade**
 * 
 * Access Pattern:
 * ProductController → InventoryFacade → InventoryService
 */
@Service
public class InventoryService implements IInventoryService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Update product stock quantity
     * Used internally by Facade to modify inventory levels
     *
     * @param product Product to update
     * @param quantityDelta Quantity change (positive for restock, negative for usage)
     */
    @Transactional
    @Override
    public void updateStock(Product product, int quantityDelta) {
        product.updateStock(quantityDelta);
        productRepository.save(product);
    }

    /**
     * Check if a product has low stock
     * Used by Facade to determine if alerts should be triggered
     *
     * @param product Product to check
     * @return True if stock is at or below threshold
     */
    public boolean isLowStock(Product product) {
        return product.getStockQuantity() <= LOW_STOCK_THRESHOLD;
    }

    /**
     * Get all products with low stock
     * Used by Facade to retrieve low-stock inventory
     *
     * @return List of products with stock <= threshold
     */
    public List<Product> getLowStockProducts() {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .filter(this::isLowStock)
                .toList();
    }

    /**
     * Get low stock threshold value
     *
     * @return Current low stock threshold
     */
    public int getLowStockThreshold() {
        return LOW_STOCK_THRESHOLD;
    }
}
package com.curatebox.service;

import com.curatebox.model.Product;
import com.curatebox.repository.ProductRepository;
import com.curatebox.service.observer.IInventoryObserver;
import com.curatebox.service.observer.LowStockAlertObserver;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InventoryService manages inventory operations and stock notifications
 * Serves as the Subject/Publisher in the Observer pattern (SUPPORTING pattern)
 * 
 * SUBSYSTEM COMPONENT OF FACADE PATTERN
 * ====================================
 * **IMPORTANT**: Observer Pattern is a SUPPORTING PATTERN used internally.
 * The PRIMARY design pattern for this module is FACADE PATTERN (InventoryFacade).
 * 
 * This service is one of the hidden subsystems coordinated by InventoryFacade:
 * - Facade Pattern (PRIMARY): InventoryFacade provides unified interface hiding this complexity
 * - Observer Pattern (SUPPORTING): Used internally by InventoryService for notifications
 * - This class (SUBSYSTEM): Manages stock and coordinates observer notifications
 * 
 * SUPPORTING PATTERN: Observer Pattern (Behavioral Pattern)
 * =========================================================
 * Purpose: Define a one-to-many dependency where if stock changes,
 *          all registered observers are notified automatically
 * 
 * Implementation Details:
 * 1. Maintains a list of registered observers (IInventoryObserver implementations)
 * 2. When stock level drops below LOW_STOCK_THRESHOLD (10 units):
 *    - All registered observers are notified via onLowStock(product) callback
 *    - Each observer can react independently (e.g., send alert, log, notify admin)
 * 3. New observers can be attached/detached without modifying InventoryService
 * 
 * Subsystem Participants:
 * - Subject (InventoryService): Maintains observer list, triggers notifications
 * - Observer Interface (IInventoryObserver): Defines notification contract
 * - Concrete Observer (LowStockAlertObserver): Implements specific notification logic
 * 
 * Benefits:
 * - Loose Coupling: Internal service doesn't need to know observer details
 * - Open/Closed Principle: New observers can be added without modifying existing code
 * - Extensibility: Easy to add new notification types behind Facade
 * 
 * Facade Access Flow:
 * 1. Client calls ProductController endpoint
 * 2. ProductController calls InventoryFacade method
 * 3. InventoryFacade internally calls this InventoryService.updateStock()
 * 4. If stock <= 10, notifyObservers() is called (internal subsystem behavior)
 * 5. Each observer's onLowStock() method is invoked
 * 6. LowStockAlertObserver logs the alert
 * 
 * **NEVER accessed directly by clients - always through InventoryFacade**
 */
@Service
public class InventoryService implements IInventoryService {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private final int lowStockThreshold = LOW_STOCK_THRESHOLD;

    // List of observers subscribed to inventory changes
    private final List<IInventoryObserver> observers = new ArrayList<>();
    
    private final ProductRepository productRepository;
    private final LowStockAlertObserver lowStockAlertObserver;

    public InventoryService(ProductRepository productRepository, LowStockAlertObserver lowStockAlertObserver) {
        this.productRepository = productRepository;
        this.lowStockAlertObserver = lowStockAlertObserver;
    }

    /**
     * Initialize service by attaching default observers
     * Called automatically after Spring constructor injection
     */
    @PostConstruct
    public void init() {
        attach(lowStockAlertObserver);
    }

    /**
     * Observer Pattern: Attach a new observer
     * Observer will be notified of low stock events
     *
     * @param observer IInventoryObserver implementation to attach
     */
    public void attach(IInventoryObserver observer) {
        observers.add(observer);
    }

    /**
     * Observer Pattern: Detach an observer
     * Observer will no longer receive notifications
     *
     * @param observer IInventoryObserver implementation to remove
     */
    public void detach(IInventoryObserver observer) {
        observers.remove(observer);
    }

    /**
     * Update product stock quantity and trigger notifications if needed
     * This is the core method that implements the Observer pattern flow
     *
     * @param product Product to update
     * @param quantityDelta Quantity change (positive for restock, negative for usage)
     */
    @Transactional
    @Override
    public void updateStock(Product product, int quantityDelta) {
        // Apply the stock update (handles negative stock prevention)
        product.updateStock(quantityDelta);
        productRepository.save(product);
        
        // Check if stock is now low and notify observers
        // Trigger alert if stock is at or below threshold (includes 0)
        if (product.getStockQuantity() <= lowStockThreshold) {
            notifyObservers(product);
        }
    }

    /**
     * Observer Pattern: Notify all observers of low stock condition
     * This is the notification mechanism that triggers all observer callbacks
     *
     * @param product Product with low stock
     */
    public void notifyObservers(Product product) {
        // Iterate through all registered observers and notify them
        observers.forEach(observer -> observer.onLowStock(product));
        product.notifyLowStock();
    }

    /**
     * Get all registered observers (for testing purposes)
     *
     * @return List of current observers
     */
    public List<IInventoryObserver> getObservers() {
        return new ArrayList<>(observers);
    }

    /**
     * Get low stock threshold value
     *
     * @return Current low stock threshold
     */
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
}
