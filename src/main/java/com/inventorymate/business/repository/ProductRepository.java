package com.inventorymate.business.repository;

import com.inventorymate.business.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long CategoryId);
    List<Product> findByCategoryIsNull();
    boolean existsByProductNameIgnoreCase(String productName);
    Product findByProductNameIgnoreCase(String productName);
    @Modifying
    @Query("UPDATE Product p SET p.category = NULL WHERE p.category.id = :categoryId")
    void updateCategoryToNull(@Param("categoryId") Long categoryId);
}
