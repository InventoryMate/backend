package com.inventorymate.business.service;

import com.inventorymate.business.dto.StockRequest;
import com.inventorymate.business.dto.StockResponse;
import com.inventorymate.business.model.Stock;

import java.util.List;

public interface StockService {
    public List<StockResponse> getAllStocks(Long storeId);
    public List<StockResponse> getAllStocksByProduct(Long productId, Long storeId);
    public Stock getStockById(Long stockId, Long storeId);
    public Stock saveStock(StockRequest stock, Long productId, Long storeId);
    public Stock updateStock(StockRequest stock, Long StockId, Long storeId);
    public void deleteStock(Long stockId, Long storeId);
}
