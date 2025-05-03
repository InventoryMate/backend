package com.inventorymate.business.dto;

import com.inventorymate.business.model.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponse {
    private Double quantity;
    private LocalDate purchaseDate;
    private LocalDate expirationDate;
    private Long productId;

    // Constructor para transformar el Stock en StockResponse
    public StockResponse(Stock stock) {
        this.quantity = stock.getQuantity();
        this.purchaseDate = stock.getPurchaseDate();
        this.expirationDate = stock.getExpirationDate();
        this.productId = stock.getProduct().getId();
    }
}