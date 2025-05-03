package com.inventorymate.business.dto;

import com.inventorymate.business.model.UnitType;
import lombok.Data;
@Data
public class ProductRequest {
    private String productName;
    private String productDescription;
    private double productPrice;
    private Long categoryId;
    private UnitType unitType;
    private boolean isExpirable;
}
