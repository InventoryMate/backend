package com.inventorymate.business.repository;

import com.inventorymate.business.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStore_Id(Long storeId);
    List<Product> findByStoreIdAndIsDeletedFalse(Long storeId);
    List<Product> findByCategoryIdAndStore_IdAndIsDeletedFalse(Long CategoryId, Long storeId);
    List<Product> findByCategoryIsNullAndStore_IdAndIsDeletedFalse(Long storeId);
    boolean existsByProductNameIgnoreCaseAndStore_IdAndIsDeletedFalse(String productName, Long storeId);
    Product findByProductNameIgnoreCaseAndStore_IdAndIsDeletedFalse(String productName, Long storeId);
    Optional<Product> findByIdAndStore_IdAndIsDeletedFalse(Long productId, Long storeId);
    boolean existsByIdAndStore_IdAndIsDeletedFalse(Long productId, Long storeId);
    List<Product> findByStore_IdAndAssignedForPredictionAndIsDeletedFalse(Long storeId, boolean assignedForPrediction);
    @Modifying
    @Query("UPDATE Product p SET p.category = NULL WHERE p.category.id = :categoryId")
    void updateCategoryToNull(@Param("categoryId") Long categoryId);
}
