package com.inventorymate.business.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockRequest {
    private Double quantity;
    private LocalDate expirationDate;
}
