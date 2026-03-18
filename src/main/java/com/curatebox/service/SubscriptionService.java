package com.curatebox.service;

import com.curatebox.model.Customer;
import com.curatebox.model.Subscription;
import com.curatebox.repository.CustomerRepository;
import com.curatebox.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, CustomerRepository customerRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.customerRepository = customerRepository;
    }

    public Subscription getSubscription(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId));
    }

    public Subscription getSubscriptionByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        return subscriptionRepository.findByCustomer(customer)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found for customer: " + customerId));
    }

    @Transactional
    public Subscription pauseSubscription(Long subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        subscription.pause();
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription resumeSubscription(Long subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        subscription.resume();
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription cancelSubscription(Long subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        subscription.cancel();
        return subscriptionRepository.save(subscription);
    }

    public String getSubscriptionStatus(Long customerId) {
        return getSubscriptionByCustomer(customerId).getStatus().name();
    }
}
