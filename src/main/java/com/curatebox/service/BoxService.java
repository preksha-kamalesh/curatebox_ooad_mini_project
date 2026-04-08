package com.curatebox.service;

import com.curatebox.model.BoxContent;
import com.curatebox.model.Customer;
import com.curatebox.model.MonthlyBox;
import com.curatebox.model.Product;
import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;
import com.curatebox.repository.CustomerRepository;
import com.curatebox.repository.MonthlyBoxRepository;
import com.curatebox.repository.ProductRepository;
import com.curatebox.repository.SubscriptionRepository;
import com.curatebox.service.factory.IBoxFactory;
import com.curatebox.service.strategy.ICurationStrategy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoxService implements IBoxService {

    private static final Set<String> ALLOWED_SHIPPING_STATUSES = Set.of("PENDING", "PACKED", "IN_TRANSIT", "DELIVERED", "SHIPPED");

    private ICurationStrategy curationStrategy;
    private final ProductRepository productRepository;
    private final MonthlyBoxRepository boxRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final IBoxFactory boxFactory;
    private final IInventoryService inventoryService;

    public BoxService(
            ICurationStrategy curationStrategy,
            ProductRepository productRepository,
            MonthlyBoxRepository boxRepository,
            SubscriptionRepository subscriptionRepository,
            CustomerRepository customerRepository,
            IBoxFactory boxFactory,
            IInventoryService inventoryService) {
        this.curationStrategy = curationStrategy;
        this.productRepository = productRepository;
        this.boxRepository = boxRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.customerRepository = customerRepository;
        this.boxFactory = boxFactory;
        this.inventoryService = inventoryService;
    }

    public void setCurationStrategy(ICurationStrategy strategy) {
        this.curationStrategy = strategy;
    }

    @Override
    public List<Product> previewCuration(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        List<Product> available = productRepository.findAll().stream().filter(Product::isInStock).toList();
        return curationStrategy.curateBox(customer, available);
    }

    @Override
    @Transactional
    public void generateMonthlyBoxes(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Curation date is required");
        }

        // Pull active subscriptions once so each customer gets curated in this run.
        List<Subscription> active = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
        List<Product> available = productRepository.findAll().stream().filter(Product::isInStock).toList();

        for (Subscription subscription : active) {
            Customer customer = subscription.getCustomer();
            List<Product> curated = curationStrategy.curateBox(customer, available);

            MonthlyBox box = boxFactory.createBox(customer, date);
            List<BoxContent> contents = new ArrayList<>();

            for (Product product : curated) {
                BoxContent content = boxFactory.createBoxContent(box, product, 1);
                contents.add(content);
                // Inventory updates stay behind an abstraction to keep service-level DIP clean.
                inventoryService.updateStock(product, -1);
            }

            box.setBoxContents(contents);
            boxRepository.save(box);
        }
    }

    @Override
    public List<MonthlyBox> getBoxesByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        return boxRepository.findByCustomer(customer);
    }

    @Override
    @Transactional
    public MonthlyBox updateShippingStatus(Long boxId, String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Shipping status is required");
        }

        // Normalize user input so API callers can send values in mixed case.
        String normalized = status.trim().toUpperCase();
        if (!ALLOWED_SHIPPING_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported shipping status: " + status);
        }

        MonthlyBox box = boxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box not found: " + boxId));
        box.updateShippingStatus(normalized);
        return boxRepository.save(box);
    }

    @Override
    @Transactional
    public MonthlyBox shipBox(Long boxId) {
        MonthlyBox box = boxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box not found: " + boxId));
        box.ship();
        return boxRepository.save(box);
    }
}
