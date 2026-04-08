package com.curatebox.service.strategy;

import com.curatebox.model.Customer;
import com.curatebox.model.CustomerPreference;
import com.curatebox.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractCurationTemplate implements ICurationStrategy {

    private final int minProducts;
    private final int maxProducts;

    protected AbstractCurationTemplate(int minProducts, int maxProducts) {
        if (minProducts < 0 || maxProducts < minProducts) {
            throw new IllegalArgumentException("Invalid curation size bounds");
        }
        this.minProducts = minProducts;
        this.maxProducts = maxProducts;
    }

    @Override
    public final List<Product> curateBox(Customer customer, List<Product> availableProducts) {
        validateInputs(customer, availableProducts);

        Set<String> likes = extractPreferenceValues(customer, true);
        Set<String> dislikes = extractPreferenceValues(customer, false);

        List<Product> filtered = filterEligibleProducts(availableProducts, dislikes);
        List<Product> ranked = rankProducts(customer, filtered, likes, dislikes);
        return applyBounds(ranked);
    }

    protected List<Product> filterEligibleProducts(List<Product> products, Set<String> dislikes) {
        return products.stream()
                .filter(Product::isInStock)
                .filter(p -> {
                    String category = normalize(p.getCategory());
                    return !category.isEmpty() && !dislikes.contains(category);
                })
                .toList();
    }

    protected abstract List<Product> rankProducts(
            Customer customer,
            List<Product> eligibleProducts,
            Set<String> likes,
            Set<String> dislikes);

    protected String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private void validateInputs(Customer customer, List<Product> availableProducts) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required for curation");
        }
        if (availableProducts == null) {
            throw new IllegalArgumentException("Available products list is required");
        }
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

    private List<Product> applyBounds(List<Product> ranked) {
        if (ranked.isEmpty()) {
            return new ArrayList<>();
        }

        int targetSize = Math.min(maxProducts, ranked.size());
        if (targetSize < minProducts) {
            return new ArrayList<>(ranked);
        }
        return new ArrayList<>(ranked.subList(0, targetSize));
    }
}
