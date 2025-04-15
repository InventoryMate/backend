package com.inventorymate.business.service;

import com.inventorymate.business.Dto.StockRequest;
import com.inventorymate.business.model.Stock;

import java.util.List;

public interface StockService {
    public List<Stock> getAllStocks(Long storeId);
    public List<Stock> getAllStocksByProduct(Long productId, Long storeId);
    public Stock getStockById(Long stockId, Long storeId);
    public Stock saveStock(StockRequest stock, Long productId, Long storeId);
    public Stock updateStock(StockRequest stock, Long StockId, Long storeId);
    public void deleteStock(Long stockId, Long storeId);
}
