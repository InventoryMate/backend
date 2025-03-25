package com.inventorymate.business.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockRequest {
    private Long productId;
    private int quantity;
    private LocalDate expirationDate;
}
