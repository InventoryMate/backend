package com.inventorymate.business.service.impl;

import com.inventorymate.business.dto.StockRequest;
import com.inventorymate.business.dto.StockResponse;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.model.Stock;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.repository.StockRepository;
import com.inventorymate.business.service.StockService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.Store;
import com.inventorymate.user.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    public StockServiceImpl(StockRepository stockRepository,
                            ProductRepository productRepository,
                            StoreRepository storeRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    public List<StockResponse> getAllStocks(Long storeId) {
        List<Stock> stocks = stockRepository.findByStore_Id(storeId);
        return stocks.stream().map(StockResponse::new).collect(Collectors.toList());
    }

    @Override
    public List<StockResponse> getAllStocksByProduct(Long productId, Long storeId) {
        List<Stock> stocks = stockRepository.findByProductIdAndStore_IdOrderByPurchaseDateAsc(productId, storeId);
        if (stocks.isEmpty()) {
            throw new ResourceNotFoundException("No stock found for product with ID: " + productId);
        }
        return stocks.stream().map(StockResponse::new).collect(Collectors.toList());
    }

    @Override
    public Stock getStockById(Long stockId, Long storeId) {
        return stockRepository.findByIdAndStore_Id(stockId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with Id " + stockId + " not found"));
    }

    @Override
    @Transactional
    public Stock saveStock(StockRequest newStock, Long productId, Long storeId) {
        return createOrUpdateStock(newStock, new Stock(), productId, storeId);
    }

    @Override
    @Transactional
    public Stock updateStock(StockRequest stockRequest, Long stockId, Long storeId) {
        Stock stockToUpdate = getStockById(stockId, storeId);
        return createOrUpdateStock(stockRequest, stockToUpdate, stockToUpdate.getProduct().getId(), storeId);
    }

    @Override
    @Transactional
    public void deleteStock(Long stockId, Long storeId) {
        Stock stock = stockRepository.findByIdAndStore_Id(stockId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        stockRepository.delete(stock);
    }

    private Stock createOrUpdateStock(StockRequest stockRequest, Stock stock, Long productId, Long storeId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with Id " + productId + " not found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        stock.setStore(store);

        stock.setProduct(product);
        stock.setQuantity(stockRequest.getQuantity());
        stock.setPurchaseDate(LocalDate.now());
        stock.setExpirationDate(stockRequest.getExpirationDate());

        validateStock(stock);

        return stockRepository.save(stock);
    }

    private void validateStock(Stock stock) {
        if (stock.getQuantity() <= 0) {
            throw new ValidationException("Quantity must be greater than 0.");
        }

        if (stock.getProduct() == null) {
            throw new ValidationException("The product associated with the stock cannot be null.");
        }

        boolean isExpirable = stock.getProduct().isExpirable();
        if (isExpirable && stock.getExpirationDate() == null) {
            throw new ValidationException("The product is perishable and requires an expiration date.");
        }

        if (isExpirable && stock.getExpirationDate().isBefore(stock.getPurchaseDate())) {
            throw new ValidationException("The expiration date must be after the purchase date.");
        }

        if (!isExpirable && stock.getExpirationDate() != null) {
            throw new ValidationException("The product is not perishable and should not have an expiration date.");
        }
    }
}