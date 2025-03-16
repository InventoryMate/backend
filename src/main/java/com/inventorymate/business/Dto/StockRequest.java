package com.inventorymate.business.Dto;

import com.inventorymate.business.model.UnitType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StockRequest {
    private Long productId;
    private int quantity;
    private UnitType unitType;
    private LocalDate expirationDate;
}
