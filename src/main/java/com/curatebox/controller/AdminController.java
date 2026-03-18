package com.curatebox.controller;

import com.curatebox.model.Admin;
import com.curatebox.repository.AdminRepository;
import com.curatebox.service.ReportService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminRepository adminRepository;
    private final ReportService reportService;

    public AdminController(AdminRepository adminRepository, ReportService reportService) {
        this.adminRepository = adminRepository;
        this.reportService = reportService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin == null || !admin.authenticate(username, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        admin.updateLastLogin();
        adminRepository.save(admin);
        return ResponseEntity.ok("Login successful");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> dashboard() {
        return ResponseEntity.ok(reportService.getSubscriberMetrics());
    }
}
