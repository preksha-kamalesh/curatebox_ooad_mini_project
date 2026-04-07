package com.curatebox.service;

import com.curatebox.model.Product;

public interface IInventoryService {
    void updateStock(Product product, int quantityDelta);
}
