package com.inventorymate.ai.controller;

import com.inventorymate.ai.dto.PredictionRequest;
import com.inventorymate.ai.dto.PredictionResponse;
import com.inventorymate.ai.service.AiPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/InventoryMate/v1/predict")
public class PredictionController {
    private final AiPredictionService aiPredictionService;

    public PredictionController(AiPredictionService aiPredictionService) {
        this.aiPredictionService = aiPredictionService;
    }

    @PostMapping
    public ResponseEntity<PredictionResponse> predict(@RequestBody PredictionRequest request) {
        PredictionResponse result = aiPredictionService.getPrediction(request);
        return ResponseEntity.ok(result);
    }
}
