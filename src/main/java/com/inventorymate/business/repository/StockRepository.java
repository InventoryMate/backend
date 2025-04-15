package com.inventorymate.business.repository;

import com.inventorymate.business.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByStore_Id (Long storeId);
    Optional<Stock> findByIdAndStore_Id (Long stockId, Long storeId);
    Boolean existsByIdAndStore_Id (Long stockId, Long storeId);
    List<Stock> findByProductIdAndStore_IdOrderByPurchaseDateAsc (Long productId, Long storeId);

    @Query("SELECT SUM(s.quantity) FROM Stock s " +
            "WHERE s.product.id = :productId " +
            "AND s.store.id = :storeId " +
            "AND (s.expirationDate IS NULL OR s.expirationDate >= CURRENT_DATE)")
    Long getAvailableStockByProductIdAndStoreId(@Param("productId") Long productId,
                                                @Param("storeId") Long storeId);

}
