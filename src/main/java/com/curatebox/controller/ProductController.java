package com.curatebox.controller;

import com.curatebox.dto.ProductDTO;
import com.curatebox.model.Product;
import com.curatebox.service.facade.InventoryFacade;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final InventoryFacade inventoryFacade;

    public ProductController(InventoryFacade inventoryFacade) {
        this.inventoryFacade = inventoryFacade;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(inventoryFacade.getAllProductsWithStock());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inventoryFacade.getAllProductsWithStock().stream()
                    .filter(p -> p.getProductId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO dto) {
        try {
            Product created = inventoryFacade.createProductWithInventory(
                    dto,
                    dto.getSupplierId(),
                    dto.getStockQuantity()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        try {
            Product existing = inventoryFacade.getAllProductsWithStock().stream()
                    .filter(p -> p.getProductId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
            existing.setProductName(dto.getProductName());
            existing.setDescription(dto.getDescription());
            existing.setCategory(dto.getCategory());
            return ResponseEntity.ok(inventoryFacade.updateProductWithSupplier(id, existing, dto.getSupplierId()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        inventoryFacade.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        return ResponseEntity.ok(inventoryFacade.getLowStockProducts());
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Integer quantity = body.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().build();
            }
            // Facade coordinates the update and notifies observers
            Product updated = inventoryFacade.updateStockWithNotification(id, quantity);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
