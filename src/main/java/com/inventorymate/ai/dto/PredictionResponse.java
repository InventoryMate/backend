package com.inventorymate.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PredictionResponse {
    @JsonProperty("predictions")
    private List<PredictionResult> results;

    @Data
    public static class PredictionResult {
        @JsonProperty("product_id")
        private String productName;

        @JsonProperty("predicted_total")
        private double predictedDemand;

        @JsonProperty("daily_predictions")
        private List<DailyPrediction> dailyPredictions;
    }

    @Data
    public static class DailyPrediction {
        private String date;

        @JsonProperty("predicted_quantity")
        private double predictedQuantity;
    }
}
