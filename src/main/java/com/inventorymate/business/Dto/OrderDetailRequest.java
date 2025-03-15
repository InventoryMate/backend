package com.inventorymate.business.Dto;

import lombok.Data;

@Data
public class OrderDetailRequest {
    private Long productId;
    private int quantity;
}
