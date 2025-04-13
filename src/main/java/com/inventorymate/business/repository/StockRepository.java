package com.inventorymate.business.repository;

import com.inventorymate.business.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByProductIdOrderByPurchaseDateAsc (Long productId);
    @Query("SELECT SUM(s.quantity) FROM Stock s WHERE s.product.id = :productId AND (s.expirationDate IS NULL OR s.expirationDate >= CURRENT_DATE)")
    Long getAvailableStockByProductId(@Param("productId") Long productId);
}
