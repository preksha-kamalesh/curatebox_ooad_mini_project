package com.curatebox.service.strategy;

import com.curatebox.model.Customer;
import com.curatebox.model.CustomerPreference;
import com.curatebox.model.Product;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PreferenceBasedCuration implements ICurationStrategy {

    private static final int MIN_PRODUCTS = 3;
    private static final int MAX_PRODUCTS = 7;

    @Override
    public List<Product> curateBox(Customer customer, List<Product> availableProducts) {
        Set<String> likes = customer.getPreferences().stream()
                .filter(CustomerPreference::isLike)
                .map(CustomerPreference::getPreferenceValue)
                .filter(v -> v != null && !v.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<String> dislikes = customer.getPreferences().stream()
                .filter(CustomerPreference::isDislike)
                .map(CustomerPreference::getPreferenceValue)
                .filter(v -> v != null && !v.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<Product> filtered = availableProducts.stream()
                .filter(Product::isInStock)
                .filter(p -> !dislikes.contains(p.getCategory().toLowerCase()))
                .sorted(Comparator.comparing((Product p) -> likes.contains(p.getCategory().toLowerCase())).reversed())
                .toList();

        int targetSize = Math.min(MAX_PRODUCTS, filtered.size());
        if (targetSize < MIN_PRODUCTS) {
            return new ArrayList<>(filtered);
        }
        return new ArrayList<>(filtered.subList(0, targetSize));
    }
}
