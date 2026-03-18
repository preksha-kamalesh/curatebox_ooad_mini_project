package com.curatebox.service.observer;

import com.curatebox.model.Product;

public interface IInventoryObserver {
    void onLowStock(Product product);
}
