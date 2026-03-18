package com.curatebox.controller;

import com.curatebox.model.MonthlyBox;
import com.curatebox.model.Product;
import com.curatebox.service.ReportService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopShippedProducts() {
        return ResponseEntity.ok(reportService.getTopShippedProducts());
    }

    @GetMapping("/customer/{id}/history")
    public ResponseEntity<List<MonthlyBox>> getCustomerOrderHistory(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getCustomerOrderHistory(id));
    }

    @GetMapping("/safe-products")
    public ResponseEntity<List<Product>> getSafeProducts(@RequestParam("allergen") String allergen) {
        return ResponseEntity.ok(reportService.getSafeProducts(allergen));
    }

    @GetMapping("/subscriber-metrics")
    public ResponseEntity<Map<String, Long>> getSubscriberMetrics() {
        return ResponseEntity.ok(reportService.getSubscriberMetrics());
    }

    @GetMapping("/shipping-summary")
    public ResponseEntity<Map<String, Long>> getShippingStatusSummary() {
        return ResponseEntity.ok(reportService.getShippingStatusSummary());
    }
}
