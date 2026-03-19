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
 * InventoryService implements the Observer Design Pattern
 * Serves as the Subject/Publisher in the Observer pattern
 * 
 * DESIGN PATTERN: Observer Pattern (Behavioral Pattern)
 * =====================================================
 * Purpose: Define a one-to-many dependency where if a subject changes state,
 *          all its observers are notified automatically
 * 
 * Implementation Details:
 * 1. Maintains a list of registered observers (IInventoryObserver implementations)
 * 2. When stock level drops below LOW_STOCK_THRESHOLD (10 units):
 *    - All registered observers are notified via onLowStock(product) callback
 *    - Each observer can react independently (e.g., send alert, log, notify admin)
 * 3. New observers can be attached/detached without modifying InventoryService
 * 
 * Participants:
 * - Subject (InventoryService): Maintains observer list, triggers notifications
 * - Observer Interface (IInventoryObserver): Defines notification contract
 * - Concrete Observer (LowStockAlertObserver): Implements specific notification logic
 * 
 * Benefits:
 * - Loose Coupling: Inventory service doesn't need to know observer details
 * - Open/Closed Principle: New observers can be added without modifying existing code
 * - Extensibility: Easy to add new notification types (email, SMS, push notifications)
 * 
 * Usage Flow:
 * 1. Admin calls PUT /api/products/{id}/stock to update stock quantity
 * 2. InventoryService.updateStock() is called
 * 3. If stock <= 10, notifyObservers() is called
 * 4. Each observer's onLowStock() method is invoked
 * 5. LowStockAlertObserver logs the alert message
 */
@Service
public class InventoryService {

    private static final int LOW_STOCK_THRESHOLD = 10;

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
    public void updateStock(Product product, int quantityDelta) {
        // Apply the stock update (handles negative stock prevention)
        product.updateStock(quantityDelta);
        productRepository.save(product);
        
        // Check if stock is now low and notify observers
        // Trigger alert if stock is at or below threshold (includes 0)
        if (product.getStockQuantity() <= LOW_STOCK_THRESHOLD) {
            notifyObservers(product);
        }
    }

    /**
     * Observer Pattern: Notify all observers of low stock condition
     * This is the notification mechanism that triggers all observer callbacks
     *
     * @param product Product with low stock
     */
    private void notifyObservers(Product product) {
        // Iterate through all registered observers and notify them
        observers.forEach(observer -> observer.onLowStock(product));
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
        return LOW_STOCK_THRESHOLD;
    }
}
