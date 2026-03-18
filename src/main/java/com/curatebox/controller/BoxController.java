package com.curatebox.controller;

import com.curatebox.model.Customer;
import com.curatebox.model.MonthlyBox;
import com.curatebox.repository.CustomerRepository;
import com.curatebox.repository.MonthlyBoxRepository;
import com.curatebox.service.BoxService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boxes")
public class BoxController {

    private final BoxService boxService;
    private final MonthlyBoxRepository monthlyBoxRepository;
    private final CustomerRepository customerRepository;

    public BoxController(BoxService boxService, MonthlyBoxRepository monthlyBoxRepository, CustomerRepository customerRepository) {
        this.boxService = boxService;
        this.monthlyBoxRepository = monthlyBoxRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateMonthlyBoxes(@RequestParam String date) {
        boxService.generateMonthlyBoxes(LocalDate.parse(date));
        return ResponseEntity.ok("Monthly boxes generated for " + date);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<MonthlyBox>> getBoxesByCustomer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
        return ResponseEntity.ok(monthlyBoxRepository.findByCustomer(customer));
    }

    @PutMapping("/{boxId}/status")
    public ResponseEntity<MonthlyBox> updateShippingStatus(@PathVariable Long boxId, @RequestBody Map<String, String> body) {
        MonthlyBox box = monthlyBoxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box not found: " + boxId));
        box.updateShippingStatus(body.getOrDefault("status", "PENDING"));
        return ResponseEntity.ok(monthlyBoxRepository.save(box));
    }

    @PutMapping("/{boxId}/ship")
    public ResponseEntity<MonthlyBox> ship(@PathVariable Long boxId) {
        MonthlyBox box = monthlyBoxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box not found: " + boxId));
        box.ship();
        return ResponseEntity.ok(monthlyBoxRepository.save(box));
    }
}
