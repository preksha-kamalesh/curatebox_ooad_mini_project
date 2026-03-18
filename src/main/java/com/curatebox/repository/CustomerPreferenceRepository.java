package com.curatebox.repository;

import com.curatebox.model.Customer;
import com.curatebox.model.CustomerPreference;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerPreferenceRepository extends JpaRepository<CustomerPreference, Long> {
    List<CustomerPreference> findByCustomer(Customer customer);
    List<CustomerPreference> findByCustomerAndLiked(Customer customer, boolean isLike);

    default List<CustomerPreference> findByCustomerAndIsLike(Customer customer, boolean isLike) {
        return findByCustomerAndLiked(customer, isLike);
    }
}
