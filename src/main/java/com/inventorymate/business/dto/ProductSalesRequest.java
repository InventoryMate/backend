package com.inventorymate.business.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductSalesRequest {
    private List<Long> productIds;
}
