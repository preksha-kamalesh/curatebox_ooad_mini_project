package com.curatebox.repository;

import com.curatebox.model.Customer;
import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByCustomer(Customer customer);
    List<Subscription> findByStatus(SubscriptionStatus status);
}
