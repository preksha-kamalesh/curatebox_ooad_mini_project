package com.curatebox.service.strategy;

import com.curatebox.model.Customer;
import com.curatebox.model.CustomerPreference;
import com.curatebox.model.Product;
import com.curatebox.service.builder.CurationSelection;
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
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required for curation");
        }
        if (availableProducts == null) {
            throw new IllegalArgumentException("Available products list is required");
        }

        Set<String> likes = extractPreferenceValues(customer, true);
        Set<String> dislikes = extractPreferenceValues(customer, false);

        Comparator<Product> ranking = Comparator
                .comparing((Product p) -> likes.contains(normalize(p.getCategory())))
                .reversed()
                .thenComparing(Product::getStockQuantity, Comparator.reverseOrder())
                .thenComparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER);

        return new CurationSelection.Builder()
                .availableProducts(availableProducts)
                .dislikes(dislikes)
                .bounds(MIN_PRODUCTS, MAX_PRODUCTS)
                .ranking(ranking)
                .normalizer(this::normalize)
                .build()
                .getProducts();
    }

    private Set<String> extractPreferenceValues(Customer customer, boolean like) {
        if (customer.getPreferences() == null) {
            return Set.of();
        }

        return customer.getPreferences().stream()
                .filter(pref -> like ? pref.isLike() : pref.isDislike())
                .map(CustomerPreference::getPreferenceValue)
                .map(this::normalize)
                .filter(v -> !v.isBlank())
                .collect(Collectors.toSet());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
