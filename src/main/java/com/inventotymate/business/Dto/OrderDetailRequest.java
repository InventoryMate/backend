package com.inventotymate.business.Dto;

import lombok.Data;

@Data
public class OrderDetailRequest {
    private Long productId;
    private int quantity;
}
