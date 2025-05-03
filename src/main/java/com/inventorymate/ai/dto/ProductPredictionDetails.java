package com.inventorymate.ai.dto;

import lombok.Data;

@Data
public class ProductPredictionDetails {
    private Long productId;
    private double weeklySales;
}
