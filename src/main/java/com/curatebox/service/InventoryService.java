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

@Service
public class InventoryService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final List<IInventoryObserver> observers = new ArrayList<>();
    private final ProductRepository productRepository;
    private final LowStockAlertObserver lowStockAlertObserver;

    public InventoryService(ProductRepository productRepository, LowStockAlertObserver lowStockAlertObserver) {
        this.productRepository = productRepository;
        this.lowStockAlertObserver = lowStockAlertObserver;
    }

    @PostConstruct
    public void init() {
        attach(lowStockAlertObserver);
    }

    public void attach(IInventoryObserver observer) {
        observers.add(observer);
    }

    public void detach(IInventoryObserver observer) {
        observers.remove(observer);
    }

    @Transactional
    public void updateStock(Product product, int quantityDelta) {
        product.updateStock(quantityDelta);
        productRepository.save(product);
        if (product.getStockQuantity() > 0 && product.getStockQuantity() <= LOW_STOCK_THRESHOLD) {
            notifyObservers(product);
        }
    }

    private void notifyObservers(Product product) {
        observers.forEach(o -> o.onLowStock(product));
    }
}
