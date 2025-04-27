package com.inventorymate.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class PredictionResponse {
    private List<PredictionResult> results;

    @Data
    public static class PredictionResult {
        private Long productId;
        private double predictedDemand; // Demanda predicha por Azure
    }
}