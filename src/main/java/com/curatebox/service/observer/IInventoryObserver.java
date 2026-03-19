package com.curatebox.service.observer;

import com.curatebox.model.Product;

/**
 * IInventoryObserver defines the contract for observers in the Observer Pattern
 * Used by InventoryService to notify multiple observers of low stock events
 * 
 * OBSERVER PATTERN - Observer Interface
 * ====================================
 * This is the Observer/Listener interface in the Observer design pattern
 * 
 * Role:
 * - Defines the callback method that all observers must implement
 * - Enables InventoryService to notify observers without knowing their concrete types
 * - Promotes loose coupling between the subject and its observers
 * 
 * Implementations:
 * - LowStockAlertObserver: Logs low stock alerts to console
 * - (Can be extended with): EmailNotificationObserver, SMSAlertObserver, AdminDashboardUpdater, etc.
 * 
 * Design Benefits:
 * - New observer types can be added without modifying InventoryService
 * - Each observer handles notification independently
 * - Follows Open/Closed Principle
 */
public interface IInventoryObserver {
    
    /**
     * Called when a product's stock falls below the LOW_STOCK_THRESHOLD
     * Observer implementations define their specific reaction
     * 
     * @param product The product that has low stock
     */
    void onLowStock(Product product);
}

