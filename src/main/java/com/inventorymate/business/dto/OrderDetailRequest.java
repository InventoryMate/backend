package com.inventorymate.business.dto;

import com.inventorymate.business.model.UnitType;
import lombok.Data;

@Data
public class OrderDetailRequest {
    private Long productId;
    private Double quantity;
    private UnitType unitType;
}
