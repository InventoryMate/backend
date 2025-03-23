package com.inventorymate.business.controller;

import com.inventorymate.business.Dto.StockRequest;
import com.inventorymate.business.model.Stock;
import com.inventorymate.business.service.StockService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/InventoryMate/v1/stocks")
public class StockController {
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stocks
    // Method: GET
    // Description: Get all stocks
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return stocks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(stocks);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stocks/{stocksId}
    // Method: GET
    // Description: Get stock by id
    @Transactional(readOnly = true)
    @GetMapping("/{stocksId}")
    public ResponseEntity<Stock> getStockById(@PathVariable(name = "stocksId") Long stockId) {
        Stock stock = stockService.getStockById(stockId);
        return ResponseEntity.ok(stock);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stocks
    // Method: POST
    // Description: Save stock
    @Transactional
    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody StockRequest stockRequest) {
        Stock savedStock = stockService.saveStock(stockRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStock);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stocks/{stockId}
    // Method: PUT
    // Description: Update Stock
    @Transactional
    @PutMapping("/{stockId}")
    public ResponseEntity<Stock> updateStock(@PathVariable(name = "stockId") Long stockId, @RequestBody StockRequest updatedStock) {
        Stock stock = stockService.updateStock(updatedStock, stockId);
        return ResponseEntity.ok(stock);
    }


    // URL: http://localhost:8081/api/InventoryMate/v1/stocks/{stockId}
    //Method: DELETE
    // Description: Delete Stock
    @Transactional
    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> deleteStock(@PathVariable(name = "stockId") Long stockId) {
        stockService.deleteStock(stockId);
        return ResponseEntity.noContent().build();
    }

    // Global Exception Handling for Not Found & Validation Exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
