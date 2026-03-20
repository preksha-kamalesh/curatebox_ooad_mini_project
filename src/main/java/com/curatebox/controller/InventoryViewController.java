package com.curatebox.controller;

import com.curatebox.model.Product;
import com.curatebox.model.Supplier;
import com.curatebox.service.ProductService;
import com.curatebox.service.SupplierService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * InventoryViewController handles all view-related requests for inventory management
 * Serves HTML templates through Thymeleaf
 * 
 * Key Responsibilities:
 * - Fetch data from services
 * - Prepare model for template rendering
 * - Route user interface requests
 * 
 * Design Principles:
 * - Single Responsibility: Only handles view logic, no business logic
 * - Dependency Injection: Services injected through constructor
 */
@Controller
@RequestMapping("/inventory")
public class InventoryViewController {

    private final ProductService productService;
    private final SupplierService supplierService;

    public InventoryViewController(ProductService productService, SupplierService supplierService) {
        this.productService = productService;
        this.supplierService = supplierService;
    }

    /**
     * Inventory Dashboard - Overview of products and suppliers
     * Shows stats: total products, low stock count, total suppliers
     * 
     * @param model Thymeleaf model to pass data to template
     * @return inventory/dashboard.html
     */
    @GetMapping
    public String inventoryDashboard(Model model) {
        List<Product> allProducts = productService.getAllProducts();
        List<Product> lowStockProducts = productService.getLowStockProducts();
        List<Supplier> suppliers = supplierService.getAllSuppliers();

        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("lowStockCount", lowStockProducts.size());
        model.addAttribute("totalSuppliers", suppliers.size());
        model.addAttribute("recentProducts", allProducts.stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("suppliers", suppliers);

        return "inventory/dashboard";
    }

    /**
     * Products List - Detailed view of all products
     * Shows stock levels, categories, and supplier information
     * 
     * @param model Thymeleaf model to pass data to template
     * @return inventory/products.html
     */
    @GetMapping("/products")
    public String productsPage(Model model) {
        List<Product> products = productService.getAllProducts();
        
        long inStockCount = products.stream()
                .filter(p -> p.getStockQuantity() > 10)
                .count();
        long lowStockCount = products.stream()
                .filter(p -> p.getStockQuantity() <= 10 && p.getStockQuantity() > 0)
                .count();
        long outOfStockCount = products.stream()
                .filter(p -> p.getStockQuantity() == 0)
                .count();

        model.addAttribute("products", products);
        model.addAttribute("inStockCount", inStockCount);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("outOfStockCount", outOfStockCount);

        return "inventory/products";
    }

    /**
     * Suppliers List - Detailed view of all suppliers
     * Shows supplier information and products supplied
     * 
     * @param model Thymeleaf model to pass data to template
     * @return inventory/suppliers.html
     */
    @GetMapping("/suppliers")
    public String suppliersPage(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        
        long totalProducts = suppliers.stream()
                .mapToLong(s -> s.getProducts().size())
                .sum();

        model.addAttribute("suppliers", suppliers);
        model.addAttribute("totalProducts", totalProducts);

        return "inventory/suppliers";
    }
}
