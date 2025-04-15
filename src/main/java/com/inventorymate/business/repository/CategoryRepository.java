package com.inventorymate.business.repository;

import com.inventorymate.business.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByStore_Id(Long storeId);
    Optional<Category> findByIdAndStore_Id(Long categoryId, Long storeId);
    Category findByCategoryNameIgnoreCaseAndStore_Id(String categoryName, Long storeId);
    boolean existsByIdAndStore_Id(Long categoryId, Long storeId);
}
