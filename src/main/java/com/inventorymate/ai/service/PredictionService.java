package com.inventorymate.ai.service;

import com.inventorymate.ai.dto.PredictionResponse;

public interface PredictionService {

    public PredictionResponse getPrediction(Long storeId, int predictionDays);
}
