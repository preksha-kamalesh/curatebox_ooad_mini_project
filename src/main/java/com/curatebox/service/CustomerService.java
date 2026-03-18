package com.curatebox.service;

import com.curatebox.dto.CustomerPreferenceDTO;
import com.curatebox.model.Customer;
import com.curatebox.model.CustomerPreference;
import com.curatebox.model.PreferenceOption;
import com.curatebox.repository.CustomerPreferenceRepository;
import com.curatebox.repository.CustomerRepository;
import com.curatebox.repository.PreferenceOptionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerPreferenceRepository customerPreferenceRepository;
    private final PreferenceOptionRepository preferenceOptionRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerPreferenceRepository customerPreferenceRepository,
            PreferenceOptionRepository preferenceOptionRepository) {
        this.customerRepository = customerRepository;
        this.customerPreferenceRepository = customerPreferenceRepository;
        this.preferenceOptionRepository = preferenceOptionRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    @Transactional
    public Customer updateProfile(Long id, String firstName, String lastName, String email, String address) {
        Customer customer = getCustomerById(id);
        customer.updateProfile(firstName, lastName, email);
        customer.setShippingAddress(address);
        return customerRepository.save(customer);
    }

    @Transactional
    public List<CustomerPreference> updatePreferences(Long customerId, List<CustomerPreferenceDTO> preferences) {
        Customer customer = getCustomerById(customerId);

        List<CustomerPreference> current = customerPreferenceRepository.findByCustomer(customer);
        customerPreferenceRepository.deleteAll(current);

        List<CustomerPreference> updated = preferences.stream().map(dto -> {
            PreferenceOption option = preferenceOptionRepository.findById(dto.getPreferenceOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("Preference option not found: " + dto.getPreferenceOptionId()));
            CustomerPreference cp = new CustomerPreference();
            cp.setCustomer(customer);
            cp.setPreferenceOption(option);
            cp.setLike(dto.isLike());
            return cp;
        }).toList();

        return customerPreferenceRepository.saveAll(updated);
    }

    public List<CustomerPreference> getCustomerPreferences(Long customerId) {
        return customerPreferenceRepository.findByCustomer(getCustomerById(customerId));
    }
}
