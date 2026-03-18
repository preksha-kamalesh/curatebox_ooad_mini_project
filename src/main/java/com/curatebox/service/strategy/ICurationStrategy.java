package com.curatebox.service.strategy;

import com.curatebox.model.Customer;
import com.curatebox.model.Product;
import java.util.List;

public interface ICurationStrategy {
    List<Product> curateBox(Customer customer, List<Product> availableProducts);
}
