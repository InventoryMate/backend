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
                .orElseThrow(() -> new ResourceNotFoundException("Stock with ID " + stockId + " not found"));
    }

    @Override
    public Stock saveStock(StockRequest newStock) {
        return createOrUpdateStock(newStock, new Stock());
    }

    @Override
    public Stock updateStock(StockRequest updatedStock, Long stockId) {
        Stock stockToUpdate = getStockById(stockId); // Ya maneja ResourceNotFoundException
        return createOrUpdateStock(updatedStock, stockToUpdate);
    }

    @Override
    public void deleteStock(Long stockId) {
        if (!stockRepository.existsById(stockId)) {
            throw new ResourceNotFoundException("Stock with ID " + stockId + " not found");
        }
        stockRepository.deleteById(stockId);
    }

    private Stock createOrUpdateStock(StockRequest stockRequest, Stock stock) {
        Product product = productRepository.findById(stockRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto con ID " + stockRequest.getProductId() + " no encontrado"));

        stock.setProduct(product);
        stock.setQuantity(stockRequest.getQuantity());
        stock.setPurchaseDate(LocalDate.now());
        stock.setUnitType(stockRequest.getUnitType());
        stock.setExpirationDate(stockRequest.getExpirationDate());

        validateStock(stock);

        return stockRepository.save(stock);
    }

    private void validateStock(Stock stock) {
        if (stock.getQuantity() <= 0) {
            throw new ValidationException("La cantidad debe ser mayor a 0.");
        }

        if (stock.getUnitType() == null) {
            throw new ValidationException("El tipo de unidad no puede ser nulo.");
        }

        if (stock.getProduct() == null) {
            throw new ValidationException("El producto asociado al stock no puede ser nulo.");
        }

        boolean isExpirable = stock.getProduct().isExpirable();
        if (isExpirable && stock.getExpirationDate() == null) {
            throw new ValidationException("El producto es perecedero y requiere una fecha de expiración.");
        }

        if (isExpirable && stock.getExpirationDate().isBefore(stock.getPurchaseDate())) {
            throw new ValidationException("La fecha de expiración debe ser posterior a la fecha de compra.");
        }

        if (!isExpirable && stock.getExpirationDate() != null) {
            throw new ValidationException("El producto no es perecedero y no debe tener fecha de expiración.");
        }
    }
}

