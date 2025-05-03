package com.inventorymate.business.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ProductWeeklySalesResponse {
    private Long productId;
    private String productName;
    private Map<String, Double> dailySales; // Ex: "Lunes" -> 10
}
