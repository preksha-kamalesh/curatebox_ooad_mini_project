package com.curatebox.service.observer;

import com.curatebox.model.Product;

/**
 * IInventoryObserver defines the contract for observers in the Observer Pattern
 * Used by InventoryService to notify multiple observers of low stock events
 * 
 * SUBSYSTEM COMPONENT OF FACADE PATTERN
 * ====================================
 * **IMPORTANT**: Observer Pattern is a SUPPORTING PATTERN used internally.
 * The PRIMARY design pattern for this module is FACADE PATTERN (InventoryFacade).
 * 
 * This interface is part of the complex subsystem that InventoryFacade hides/coordinates:
 * - Facade Pattern (PRIMARY): InventoryFacade is the main design pattern providing unified interface
 * - Observer Pattern (SUPPORTING): Used internally by InventoryService for notifications
 * - This interface (HELPER): Part of the subsystem hidden behind Facade
 * 
 * Observer Role (as subsystem component):
 * - Defines the callback method that all observers must implement
 * - Enables InventoryService to notify observers without knowing their concrete types
 * - Promotes loose coupling between InventoryService and observers
 * 
 * Implementations (Hidden behind Facade):
 * - LowStockAlertObserver: Logs low stock alerts
 * - Future: EmailNotificationObserver, SMSAlertObserver, AdminDashboardUpdater, etc.
 * 
 * Design Benefits:
 * - New observer types can be added without modifying InventoryService
 * - Each observer handles notification independently
 * - Follows Open/Closed Principle (for extensibility within subsystem)
 * 
 * **Access Pattern**: Clients (ProductController) ONLY access through InventoryFacade.\n * The Facade coordinates when and how observers are notified.\n */
public interface IInventoryObserver {
    
    /**
     * Called when a product's stock falls below the LOW_STOCK_THRESHOLD
     * Observer implementations define their specific reaction
     * 
     * @param product The product that has low stock
     */
    void onLowStock(Product product);
}

