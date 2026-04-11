package com.curatebox.service.facade;

import com.curatebox.dto.ProductDTO;
import com.curatebox.dto.SupplierDTO;
import com.curatebox.model.Product;
import com.curatebox.model.Supplier;
import com.curatebox.repository.ProductRepository;
import com.curatebox.service.InventoryService;
import com.curatebox.service.ProductService;
import com.curatebox.service.SupplierService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryFacade {

    private final ProductService productService;
    private final SupplierService supplierService;
    private final InventoryService inventoryService;
    private final ProductRepository productRepository;

    public InventoryFacade(ProductService productService,
                          SupplierService supplierService,
                          InventoryService inventoryService,
                          ProductRepository productRepository) {
        this.productService = productService;
        this.supplierService = supplierService;
        this.inventoryService = inventoryService;
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProductWithInventory(ProductDTO dto, Long supplierId, int initialStock) {
        Product product = productService.createProduct(dto);
        
        if (supplierId != null) {
            Supplier supplier = supplierService.getSupplierById(supplierId);
            product.setSupplier(supplier);
        }
        
        product.setStockQuantity(initialStock);
        productRepository.save(product);
        
        return product;
    }

    @Transactional
    public Product updateStockWithNotification(Long productId, int quantity) {
        Product product = productService.getProductById(productId);
        inventoryService.updateStock(product, quantity);
        
        if (inventoryService.isLowStock(product)) {
            System.out.println("[LOW STOCK ALERT] Product: " + product.getProductName() + 
                             " | Stock: " + product.getStockQuantity());
        }
        
        return productService.getProductById(productId);
    }

    public List<Product> getAllProductsWithStock() {
        return productService.getAllProducts();
    }

    public List<Product> getLowStockProducts() {
        List<Product> allProducts = productService.getAllProducts();
        int threshold = inventoryService.getLowStockThreshold();
        return allProducts.stream()
                .filter(p -> p.getStockQuantity() <= threshold)
                .toList();
    }

    @Transactional
    public Supplier createSupplier(SupplierDTO dto) {
        return supplierService.createSupplier(dto);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @Transactional
    public Product updateProductWithSupplier(Long productId, Product product, Long supplierId) {
        if (supplierId != null) {
            Supplier supplier = supplierService.getSupplierById(supplierId);
            product.setSupplier(supplier);
        }
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productService.deleteProduct(productId);
    }
}
