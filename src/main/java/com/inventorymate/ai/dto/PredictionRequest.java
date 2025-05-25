package com.inventorymate.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PredictionRequest {
    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("days")
    private int predictionDays;

    @JsonProperty("products")
    private List<ProductPredictionDetails> products;

    @JsonProperty("season")
    private String season;

    @JsonProperty("date")
    private String date;
}
