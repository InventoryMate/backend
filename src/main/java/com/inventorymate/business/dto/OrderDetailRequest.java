package com.inventorymate.business.dto;

import lombok.Data;

@Data
public class OrderDetailRequest {
    private Long productId;
    private int quantity;
}
