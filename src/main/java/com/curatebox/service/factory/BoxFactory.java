package com.curatebox.service.factory;

import com.curatebox.model.BoxContent;
import com.curatebox.model.Customer;
import com.curatebox.model.MonthlyBox;
import com.curatebox.model.Product;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class BoxFactory {

    public MonthlyBox createBox(Customer customer, LocalDate curationDate) {
        MonthlyBox box = new MonthlyBox();
        box.setCustomer(customer);
        box.setCurationDate(curationDate);
        box.setShippingStatus("PENDING");
        return box;
    }

    public BoxContent createBoxContent(MonthlyBox box, Product product, int quantity) {
        BoxContent content = new BoxContent();
        content.setMonthlyBox(box);
        content.setProduct(product);
        content.setQuantity(quantity);
        return content;
    }
}
