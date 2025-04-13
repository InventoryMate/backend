package com.inventorymate.business.service;

import com.inventorymate.business.Dto.StockRequest;
import com.inventorymate.business.model.Stock;

import java.util.List;

public interface StockService {
    public List<Stock> getAllStocks();
    public List<Stock> getAllStocksByProduct(Long productId);
    public Stock getStockById(Long stockId);
    public Stock saveStock(StockRequest stock, long productId);
    public Stock updateStock(StockRequest stock, Long StockId);
    public void deleteStock(Long stockId);
}
