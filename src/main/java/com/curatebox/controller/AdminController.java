package com.curatebox.controller;

import com.curatebox.service.IAdminAuthService;
import com.curatebox.service.ReportService;
import jakarta.servlet.http.HttpSession;
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

    private final IAdminAuthService adminAuthService;
    private final ReportService reportService;

    public AdminController(IAdminAuthService adminAuthService, ReportService reportService) {
        this.adminAuthService = adminAuthService;
        this.reportService = reportService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            boolean success = adminAuthService.login(username, password);
            if (!success) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
            }

            session.setAttribute("isAdminLoggedIn", true);
            session.setAttribute("adminUsername", username);

            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> dashboard() {
        return ResponseEntity.ok(reportService.getSubscriberMetrics());
    }
}
