package com.curatebox.service.builder;

import com.curatebox.model.Product;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CurationSelection {

    private final List<Product> products;

    private CurationSelection(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public static class Builder {
        private List<Product> availableProducts;
        private Set<String> dislikes = Set.of();
        private int minProducts;
        private int maxProducts;
        private Comparator<Product> ranking;
        private Function<String, String> normalizer = value -> value == null ? "" : value.trim().toLowerCase();

        public Builder availableProducts(List<Product> availableProducts) {
            this.availableProducts = availableProducts;
            return this;
        }

        public Builder dislikes(Set<String> dislikes) {
            this.dislikes = dislikes == null ? Set.of() : dislikes;
            return this;
        }

        public Builder bounds(int minProducts, int maxProducts) {
            if (minProducts < 0 || maxProducts < minProducts) {
                throw new IllegalArgumentException("Invalid curation size bounds");
            }
            this.minProducts = minProducts;
            this.maxProducts = maxProducts;
            return this;
        }

        public Builder ranking(Comparator<Product> ranking) {
            this.ranking = ranking;
            return this;
        }

        public Builder normalizer(Function<String, String> normalizer) {
            if (normalizer != null) {
                this.normalizer = normalizer;
            }
            return this;
        }

        public CurationSelection build() {
            if (availableProducts == null) {
                throw new IllegalArgumentException("Available products list is required");
            }
            if (ranking == null) {
                throw new IllegalArgumentException("Ranking strategy is required");
            }

            List<Product> filtered = availableProducts.stream()
                    .filter(Product::isInStock)
                    .filter(p -> {
                        String category = normalizer.apply(p.getCategory());
                        return !category.isEmpty() && !dislikes.contains(category);
                    })
                    .toList();

            List<Product> ranked = filtered.stream().sorted(ranking).toList();
            if (ranked.isEmpty()) {
                return new CurationSelection(new ArrayList<>());
            }

            int targetSize = Math.min(maxProducts, ranked.size());
            if (targetSize < minProducts) {
                return new CurationSelection(new ArrayList<>(ranked));
            }

            return new CurationSelection(new ArrayList<>(ranked.subList(0, targetSize)));
        }
    }
}
