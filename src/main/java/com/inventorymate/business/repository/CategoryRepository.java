package com.inventorymate.business.repository;

import com.inventorymate.business.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoryByCategoryName(String categoryName);
    Category findByCategoryNameIgnoreCase(String categoryName);
}
