package com.inventorymate.business.Dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private LocalDateTime orderDate;
    private double totalPrice;
    private List<OrderDetailResponse> orderDetails;
}