package com.curatebox.service;

import com.curatebox.model.Admin;
import com.curatebox.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuthService implements IAdminAuthService {

    private final AdminRepository adminRepository;

    public AdminAuthService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional
    public boolean login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        Admin admin = adminRepository.findByUsername(username.trim()).orElse(null);
        if (admin == null || !admin.authenticate(username.trim(), password)) {
            return false;
        }

        // Keep login side effects in one place instead of spreading across controllers.
        admin.updateLastLogin();
        adminRepository.save(admin);
        return true;
    }
}