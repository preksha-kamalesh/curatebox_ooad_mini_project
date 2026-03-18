package com.curatebox.service.observer;

import com.curatebox.model.Product;
import org.springframework.stereotype.Component;

@Component
public class LowStockAlertObserver implements IInventoryObserver {

    @Override
    public void onLowStock(Product product) {
        System.out.println("[LOW STOCK ALERT] Product: " + product.getProductName() + " | Stock: " + product.getStockQuantity());
    }
}
