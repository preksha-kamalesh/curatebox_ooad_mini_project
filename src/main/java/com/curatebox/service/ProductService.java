package com.curatebox.service;

import com.curatebox.dto.ProductDTO;
import com.curatebox.model.Product;
import com.curatebox.model.Supplier;
import com.curatebox.repository.ProductRepository;
import com.curatebox.repository.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    @Transactional
    public Product createProduct(ProductDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + dto.getSupplierId()));

        Product p = new Product();
        p.setProductName(dto.getProductName());
        p.setDescription(dto.getDescription());
        p.setCategory(dto.getCategory());
        p.setStockQuantity(dto.getStockQuantity());
        p.setSupplier(supplier);
        return productRepository.save(p);
    }

    @Transactional
    public Product updateProduct(Long id, ProductDTO dto) {
        Product p = getProductById(id);
        p.setProductName(dto.getProductName());
        p.setDescription(dto.getDescription());
        p.setCategory(dto.getCategory());
        p.setStockQuantity(dto.getStockQuantity());
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + dto.getSupplierId()));
            p.setSupplier(supplier);
        }
        return productRepository.save(p);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findByStockQuantityLessThanEqual(10);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
