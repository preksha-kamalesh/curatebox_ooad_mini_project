package com.curatebox.controller;

import com.curatebox.dto.SupplierDTO;
import com.curatebox.model.Supplier;
import com.curatebox.service.SupplierService;
import java.util.List;
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

/**
 * SupplierController handles all supplier-related REST API endpoints
 * Implements MVC Controller pattern for supplier management
 * Follows Design Principle: Single Responsibility Principle (SRP)
 * - Responsible only for HTTP request/response handling
 * - Delegates business logic to SupplierService
 */
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * Retrieve all suppliers
     * GET /api/suppliers
     *
     * @return List of all suppliers
     */
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    /**
     * Retrieve a supplier by ID
     * GET /api/suppliers/{id}
     *
     * @param id Supplier ID
     * @return Supplier if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        try {
            Supplier supplier = supplierService.getSupplierById(id);
            return ResponseEntity.ok(supplier);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new supplier
     * POST /api/suppliers
     *
     * @param dto Supplier data transfer object
     * @return Created supplier with 201 status
     */
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody SupplierDTO dto) {
        try {
            Supplier supplier = supplierService.createSupplier(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(supplier);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing supplier
     * PUT /api/suppliers/{id}
     *
     * @param id Supplier ID to update
     * @param dto Updated supplier data
     * @return Updated supplier, 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody SupplierDTO dto) {
        try {
            Supplier supplier = supplierService.updateSupplier(id, dto);
            return ResponseEntity.ok(supplier);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a supplier
     * DELETE /api/suppliers/{id}
     *
     * @param id Supplier ID to delete
     * @return No content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
