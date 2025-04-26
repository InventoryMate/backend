package com.inventorymate.ai.dto;

import java.util.Map;

public class PredictionResponse {
    private Map<String, Integer> predictedStock;

    public Map<String, Integer> getPredictedStock() {
        return predictedStock;
    }

    public void setPredictedStock(Map<String, Integer> predictedStock) {
        this.predictedStock = predictedStock;
    }
}
