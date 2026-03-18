package com.curatebox.repository;

import com.curatebox.model.Customer;
import com.curatebox.model.MonthlyBox;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyBoxRepository extends JpaRepository<MonthlyBox, Long> {
    List<MonthlyBox> findByCustomer(Customer customer);
    List<MonthlyBox> findByCurationDate(LocalDate date);
}
