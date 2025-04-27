package com.inventorymate.ai.service;

public interface ProductPredictionService {

    public void assignProductForPrediction(Long productId, Long storeId, double weeklySales);
    public void unassignProductForPrediction(Long productId, Long storeId);
}
