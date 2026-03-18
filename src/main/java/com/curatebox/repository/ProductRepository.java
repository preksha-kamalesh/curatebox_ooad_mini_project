package com.curatebox.repository;

import com.curatebox.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByStockQuantityLessThanEqual(int threshold);
    List<Product> findByCategoryIn(List<String> categories);
}
