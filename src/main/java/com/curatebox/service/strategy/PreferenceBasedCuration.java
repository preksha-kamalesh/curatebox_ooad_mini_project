package com.curatebox.service.strategy;

import com.curatebox.model.Customer;
import com.curatebox.model.Product;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PreferenceBasedCuration extends AbstractCurationTemplate {

    private static final int MIN_PRODUCTS = 3;
    private static final int MAX_PRODUCTS = 7;

    public PreferenceBasedCuration() {
        super(MIN_PRODUCTS, MAX_PRODUCTS);
    }

    @Override
    protected List<Product> rankProducts(
            Customer customer,
            List<Product> eligibleProducts,
            Set<String> likes,
            Set<String> dislikes) {
        return eligibleProducts.stream()
                .sorted(Comparator
                        .comparing((Product p) -> likes.contains(normalize(p.getCategory())))
                        .reversed()
                        .thenComparing(Product::getStockQuantity, Comparator.reverseOrder())
                        .thenComparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
