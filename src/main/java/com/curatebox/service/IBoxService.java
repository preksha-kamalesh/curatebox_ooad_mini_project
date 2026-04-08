package com.curatebox.service;

import com.curatebox.model.MonthlyBox;
import com.curatebox.model.Product;
import java.time.LocalDate;
import java.util.List;

public interface IBoxService {
    void generateMonthlyBoxes(LocalDate date);
    List<Product> previewCuration(Long customerId);
    List<MonthlyBox> getBoxesByCustomer(Long customerId);
    MonthlyBox updateShippingStatus(Long boxId, String status);
    MonthlyBox shipBox(Long boxId);
}
