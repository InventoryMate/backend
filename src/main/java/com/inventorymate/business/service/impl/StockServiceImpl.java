package com.inventorymate.business.service.impl;

import com.inventorymate.business.Dto.StockRequest;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.model.Stock;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.repository.StockRepository;
import com.inventorymate.business.service.StockService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    public StockServiceImpl(StockRepository stockRepository, ProductRepository productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public Stock getStockById(Long stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with Id " + stockId + " not found"));
    }

    @Override
    public Stock saveStock(StockRequest newStock) {
        return createOrUpdateStock(newStock, new Stock());
    }

    @Override
    public Stock updateStock(StockRequest stockRequest, Long stockId) {
        Stock stockToUpdate = getStockById(stockId);
        return createOrUpdateStock(stockRequest, stockToUpdate);
    }

    @Override
    public void deleteStock(Long stockId) {
        if (!stockRepository.existsById(stockId)) {
            throw new ResourceNotFoundException("Stock with Id " + stockId + " not found");
        }
        stockRepository.deleteById(stockId);
    }

    private Stock createOrUpdateStock(StockRequest stockRequest, Stock stock) {
        Product product = productRepository.findById(stockRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with Id " + stockRequest.getProductId() + " not found"));

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

