package com.curatebox.controller;

import com.curatebox.dto.CustomerDTO;
import com.curatebox.dto.CustomerPreferenceDTO;
import com.curatebox.model.Customer;
import com.curatebox.model.CustomerPreference;
import com.curatebox.service.CustomerService;
import com.curatebox.service.SubscriptionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;

    public CustomerController(CustomerService customerService, SubscriptionService subscriptionService) {
        this.customerService = customerService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(customerService.getCustomerById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO dto) {
        try {
            Customer updated = customerService.updateProfile(id, dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getShippingAddress());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<List<CustomerPreference>> updatePreferences(@PathVariable Long id, @RequestBody List<CustomerPreferenceDTO> preferences) {
        try {
            return ResponseEntity.ok(customerService.updatePreferences(id, preferences));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/preferences")
    public ResponseEntity<List<CustomerPreference>> getCustomerPreferences(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(customerService.getCustomerPreferences(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/subscription")
    public ResponseEntity<String> getSubscriptionStatus(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(subscriptionService.getSubscriptionStatus(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
