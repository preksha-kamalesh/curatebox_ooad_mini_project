package com.curatebox.service;
import com.curatebox.model.Product;
import com.curatebox.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService implements IInventoryService {
    private static final int LOW_STOCK_THRESHOLD = 10;
    private final ProductRepository productRepository;
    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Transactional
    @Override
    public void updateStock(Product product, int quantityDelta) {
        product.updateStock(quantityDelta);
        productRepository.save(product);
    }
    public boolean isLowStock(Product product) {
        return product.getStockQuantity() <= LOW_STOCK_THRESHOLD;
    }
    public List<Product> getLowStockProducts() {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream().filter(this::isLowStock).toList();
    }
    public int getLowStockThreshold() {
        return LOW_STOCK_THRESHOLD;
    }
}