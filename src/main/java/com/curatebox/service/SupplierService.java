package com.curatebox.service;

import com.curatebox.dto.SupplierDTO;
import com.curatebox.model.Supplier;
import com.curatebox.repository.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SupplierService handles business logic for supplier management
 * Implements the Single Responsibility Principle (Design Principle)
 * - Responsible ONLY for supplier-related operations
 * - Delegates persistence to SupplierRepository
 * - Follows DIP by depending on abstractions (Repository interface)
 */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    /**
     * Retrieve all suppliers
     *
     * @return List of all suppliers in the system
     */
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    /**
     * Retrieve a specific supplier by ID
     *
     * @param id Supplier ID
     * @return Supplier object if found
     * @throws IllegalArgumentException if supplier not found
     */
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + id));
    }

    /**
     * Create a new supplier
     * Applies Dependency Inversion Principle - depends on SupplierRepository abstraction
     *
     * @param dto Supplier data transfer object
     * @return Created supplier entity
     */
    @Transactional
    public Supplier createSupplier(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactEmail(dto.getContactEmail());
        supplier.setContactPhone(dto.getContactPhone());
        return supplierRepository.save(supplier);
    }

    /**
     * Update an existing supplier
     *
     * @param id Supplier ID to update
     * @param dto Updated supplier data
     * @return Updated supplier entity
     * @throws IllegalArgumentException if supplier not found
     */
    @Transactional
    public Supplier updateSupplier(Long id, SupplierDTO dto) {
        Supplier supplier = getSupplierById(id);
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactEmail(dto.getContactEmail());
        supplier.setContactPhone(dto.getContactPhone());
        return supplierRepository.save(supplier);
    }

    /**
     * Delete a supplier by ID
     *
     * @param id Supplier ID to delete
     */
    @Transactional
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }
}
