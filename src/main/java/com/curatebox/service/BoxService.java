package com.curatebox.service;

import com.curatebox.model.BoxContent;
import com.curatebox.model.Customer;
import com.curatebox.model.MonthlyBox;
import com.curatebox.model.Product;
import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;
import com.curatebox.repository.MonthlyBoxRepository;
import com.curatebox.repository.ProductRepository;
import com.curatebox.repository.SubscriptionRepository;
import com.curatebox.service.factory.BoxFactory;
import com.curatebox.service.strategy.ICurationStrategy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoxService {

    private ICurationStrategy curationStrategy;
    private final ProductRepository productRepository;
    private final MonthlyBoxRepository boxRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BoxFactory boxFactory;
    private final InventoryService inventoryService;

    public BoxService(
            ICurationStrategy curationStrategy,
            ProductRepository productRepository,
            MonthlyBoxRepository boxRepository,
            SubscriptionRepository subscriptionRepository,
            BoxFactory boxFactory,
            InventoryService inventoryService) {
        this.curationStrategy = curationStrategy;
        this.productRepository = productRepository;
        this.boxRepository = boxRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.boxFactory = boxFactory;
        this.inventoryService = inventoryService;
    }

    public void setCurationStrategy(ICurationStrategy strategy) {
        this.curationStrategy = strategy;
    }

    @Transactional
    public void generateMonthlyBoxes(LocalDate date) {
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
                inventoryService.updateStock(product, -1);
            }

            box.setBoxContents(contents);
            boxRepository.save(box);
        }
    }
}
