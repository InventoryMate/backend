package com.inventorymate.business.Dto;

import lombok.Data;
@Data
public class ProductRequest {
    private String productName;
    private String productDescription;
    private double productPrice;
    private Long categoryId;
    private boolean isExpirable;
}
