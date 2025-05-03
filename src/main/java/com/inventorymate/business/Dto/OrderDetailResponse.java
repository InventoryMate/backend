package com.inventorymate.business.dto;

import lombok.Data;

@Data
public class OrderDetailResponse {
    private Long productId;
    private String productName;
    private double quantity;
    private double subtotalPrice;
}