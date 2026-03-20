package com.curatebox.service.observer;

import com.curatebox.model.Product;
import org.springframework.stereotype.Component;

/**
 * LowStockAlertObserver is a concrete implementation of the Observer pattern
 * Handles low stock notifications by logging alerts
 * 
 * OBSERVER PATTERN - Concrete Observer
 * ===================================
 * Purpose: React to low stock events by logging alert messages
 * 
 * This observer:
 * - Implements IInventoryObserver interface
 * - Gets notified by InventoryService when stock is low
 * - Logs formatted alert message with product details
 * 
 * Current Implementation: Console logging
 * Future Enhancement Possibilities:
 * - Send email notifications to suppliers
 * - Update admin dashboard in real-time
 * - Send SMS alerts to warehouse managers
 * - Create tickets in inventory management system
 * - Trigger automatic reorder process
 * 
 * Note: Spring @Component makes this a singleton bean, automatically
 * injected into InventoryService on application startup
 */
@Component
public class LowStockAlertObserver implements IInventoryObserver {

    /**
     * Receives notification when a product's stock drops below threshold
     * Logs alert message to system console/logs
     * 
     * Flow:
     * 1. Admin updates stock via API -> calls InventoryService.updateStock()
     * 2. InventoryService detects stock <= 10
     * 3. InventoryService identifies this product and calls notifyObservers()
     * 4. This method (onLowStock) is invoked for this product
     * 5. Alert message is logged
     *
     * @param product The product with low stock
     */
    @Override
    public void onLowStock(Product product) {
        String alertMessage = String.format(
            "[LOW STOCK ALERT] Product: %s | Stock: %d | Category: %s | Supplier: %s",
            product.getProductName(),
            product.getStockQuantity(),
            product.getCategory(),
            product.getSupplier() != null ? product.getSupplier().getSupplierName() : "Unknown"
        );
        
        System.out.println(alertMessage);
        
        // In production, this could be enhanced with:
        // - logger.warn(alertMessage);
        // - sendEmailAlert(product);
        // - updateAdminDashboard(product);
        // - triggerAutomaticReorder(product);
    }
}

