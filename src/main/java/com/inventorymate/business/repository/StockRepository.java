package com.inventorymate.business.repository;

import com.inventorymate.business.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByProductIdOrderByPurchaseDateAsc (Long productId);
}
