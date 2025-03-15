package com.inventorymate.business.Dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private List<OrderDetailRequest> orderDetails;
}
