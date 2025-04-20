package com.inventorymate.business.Dto;

import lombok.Data;

@Data
public class OrderDetailResponse {
    private Long productId;
    private String productName;
    private int quantity;
    private double subtotalPrice;
}