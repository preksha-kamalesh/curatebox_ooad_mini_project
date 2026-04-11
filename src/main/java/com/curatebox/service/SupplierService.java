package com.curatebox.service;

import com.curatebox.dto.SupplierDTO;
import com.curatebox.model.Supplier;
import com.curatebox.repository.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + id));
    }

    @Transactional
    public Supplier createSupplier(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactEmail(dto.getContactEmail());
        supplier.setContactPhone(dto.getContactPhone());
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, SupplierDTO dto) {
        Supplier supplier = getSupplierById(id);
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactEmail(dto.getContactEmail());
        supplier.setContactPhone(dto.getContactPhone());
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }
}
