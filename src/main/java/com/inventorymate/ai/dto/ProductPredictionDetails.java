package com.inventorymate.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class ProductPredictionDetails {
    private Long productId;
    private double weeklySales;
    private double price;
    private Long categoryId;
}
