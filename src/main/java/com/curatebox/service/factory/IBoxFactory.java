package com.curatebox.service.factory;

import com.curatebox.model.BoxContent;
import com.curatebox.model.Customer;
import com.curatebox.model.MonthlyBox;
import com.curatebox.model.Product;
import java.time.LocalDate;

public interface IBoxFactory {
    MonthlyBox createBox(Customer customer, LocalDate curationDate);
    BoxContent createBoxContent(MonthlyBox box, Product product, int quantity);
}
