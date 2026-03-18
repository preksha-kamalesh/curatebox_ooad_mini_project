package com.curatebox.repository;

import com.curatebox.model.PreferenceOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceOptionRepository extends JpaRepository<PreferenceOption, Long> {
    List<PreferenceOption> findByCategory(String category);
}
