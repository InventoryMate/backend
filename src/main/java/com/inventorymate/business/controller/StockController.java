package com.inventorymate.business.controller;

import com.inventorymate.business.Dto.StockRequest;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.model.Stock;
import com.inventorymate.business.service.StockService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/InventoryMate/v1")
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
    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Stock> stocks = stockService.getAllStocks(userDetails.getStoreId());
        return stocks.isEmpty()? ResponseEntity.noContent().build() : ResponseEntity.ok(stocks);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products/{productId}/stocks
    // Method: GET
    // Description: Get all stocks
    @Transactional(readOnly = true)
    @GetMapping("/products/{productId}/stocks")
    public ResponseEntity<List<Stock>> getAllStocksByProduct(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @PathVariable(name = "productId") Long productId) {
        List<Stock> stocks = stockService.getAllStocksByProduct(productId, userDetails.getStoreId());
        return stocks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(stocks);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stocks/{stocksId}
    // Method: GET
    // Description: Get stock by id
    @Transactional(readOnly = true)
    @GetMapping("/stocks/{stocksId}")
    public ResponseEntity<Stock> getStockById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable(name = "stocksId") Long stockId) {
        Stock stock = stockService.getStockById(stockId, userDetails.getStoreId());
        return ResponseEntity.ok(stock);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stocks
    // Method: POST
    // Description: Save stock
    @Transactional
    @PostMapping("/products/{productId}/stocks")
    public ResponseEntity<Stock> createStock(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable(name = "productId") Long productId,
                                             @RequestBody StockRequest stockRequest) {
        Stock savedStock = stockService.saveStock(stockRequest, productId, userDetails.getStoreId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStock);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stocks/{stockId}
    // Method: PUT
    // Description: Update Stock
    @Transactional
    @PutMapping("/stocks/{stockId}")
    public ResponseEntity<Stock> updateStock(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable(name = "stockId") Long stockId,
                                             @RequestBody StockRequest updatedStock) {
        Stock stock = stockService.updateStock(updatedStock, stockId, userDetails.getStoreId());
        return ResponseEntity.ok(stock);
    }


    // URL: http://localhost:8081/api/InventoryMate/v1/stocks/{stockId}
    //Method: DELETE
    // Description: Delete Stock
    @Transactional
    @DeleteMapping("/stocks/{stockId}")
    public ResponseEntity<Void> deleteStock(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable(name = "stockId") Long stockId) {
        stockService.deleteStock(stockId, userDetails.getStoreId());
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
