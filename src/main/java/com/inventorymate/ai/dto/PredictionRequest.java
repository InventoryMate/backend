package com.inventorymate.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class PredictionRequest {
    private List<String> productIds;
    private int numberOfDays;
    private Long storeId;
    private int season;
}