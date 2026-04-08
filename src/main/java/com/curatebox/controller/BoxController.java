package com.curatebox.controller;

import com.curatebox.model.MonthlyBox;
import com.curatebox.model.Product;
import com.curatebox.service.IBoxService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
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

    private final IBoxService boxService;

    public BoxController(IBoxService boxService) {
        this.boxService = boxService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateMonthlyBoxes(@RequestParam String date) {
        try {
            // Keep date parsing at the edge so service receives a typed LocalDate.
            LocalDate parsedDate = LocalDate.parse(date);
            boxService.generateMonthlyBoxes(parsedDate);
            return ResponseEntity.ok(Map.of("message", "Monthly boxes generated for " + parsedDate));
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Date must be in ISO format: yyyy-MM-dd"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<?> getBoxesByCustomer(@PathVariable Long id) {
        try {
            List<MonthlyBox> boxes = boxService.getBoxesByCustomer(id);
            return ResponseEntity.ok(boxes);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/customer/{id}/preview")
    public ResponseEntity<?> previewCuration(@PathVariable Long id) {
        try {
            List<Product> curatedProducts = boxService.previewCuration(id);
            return ResponseEntity.ok(curatedProducts);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{boxId}/status")
    public ResponseEntity<?> updateShippingStatus(@PathVariable Long boxId, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            MonthlyBox updated = boxService.updateShippingStatus(boxId, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            HttpStatus statusCode = ex.getMessage() != null && ex.getMessage().contains("not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(statusCode).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{boxId}/ship")
    public ResponseEntity<?> ship(@PathVariable Long boxId) {
        try {
            MonthlyBox shipped = boxService.shipBox(boxId);
            return ResponseEntity.ok(shipped);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }
}
