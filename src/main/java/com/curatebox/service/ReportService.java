package com.curatebox.service;

import com.curatebox.model.BoxContent;
import com.curatebox.model.MonthlyBox;
import com.curatebox.model.Product;
import com.curatebox.model.SubscriptionStatus;
import com.curatebox.repository.BoxContentRepository;
import com.curatebox.repository.CustomerRepository;
import com.curatebox.repository.MonthlyBoxRepository;
import com.curatebox.repository.ProductRepository;
import com.curatebox.repository.SubscriptionRepository;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final BoxContentRepository boxContentRepository;
    private final MonthlyBoxRepository monthlyBoxRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ReportService(
            BoxContentRepository boxContentRepository,
            MonthlyBoxRepository monthlyBoxRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            SubscriptionRepository subscriptionRepository) {
        this.boxContentRepository = boxContentRepository;
        this.monthlyBoxRepository = monthlyBoxRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<Map<String, Object>> getTopShippedProducts() {
        Map<Product, Long> counts = boxContentRepository.findAll().stream()
                .collect(Collectors.groupingBy(BoxContent::getProduct, Collectors.counting()));

        return counts.entrySet().stream()
                .sorted(Map.Entry.<Product, Long>comparingByValue(Comparator.reverseOrder()))
                .map(e -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("productId", e.getKey().getProductId());
                    row.put("productName", e.getKey().getProductName());
                    row.put("shipCount", e.getValue());
                    return row;
                })
                .toList();
    }

    public List<MonthlyBox> getCustomerOrderHistory(Long customerId) {
        return monthlyBoxRepository.findByCustomer(customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId)));
    }

    public List<Product> getSafeProducts(String allergen) {
        return productRepository.findAll().stream()
                .filter(p -> !p.getCategory().equalsIgnoreCase(allergen))
                .toList();
    }

    public Map<String, Long> getSubscriberMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("ACTIVE", (long) subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE).size());
        metrics.put("PAUSED", (long) subscriptionRepository.findByStatus(SubscriptionStatus.PAUSED).size());
        metrics.put("CANCELLED", (long) subscriptionRepository.findByStatus(SubscriptionStatus.CANCELLED).size());
        return metrics;
    }

    public Map<String, Long> getShippingStatusSummary() {
        Map<String, Long> summary = new HashMap<>();
        monthlyBoxRepository.findAll().forEach(b -> summary.merge(b.getShippingStatus(), 1L, Long::sum));
        summary.putIfAbsent("PENDING", 0L);
        summary.putIfAbsent("SHIPPED", 0L);
        summary.putIfAbsent("DELIVERED", 0L);
        return summary;
    }
}
