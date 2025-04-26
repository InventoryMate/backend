package com.inventorymate.ai.service;

import com.inventorymate.ai.dto.PredictionRequest;
import com.inventorymate.ai.dto.PredictionResponse;

public interface AiPredictionService {

    PredictionResponse getPrediction(PredictionRequest request);
}
