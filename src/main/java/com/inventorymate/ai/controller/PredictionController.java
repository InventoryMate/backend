package com.inventorymate.ai.controller;

import com.inventorymate.ai.dto.PredictionRequest;
import com.inventorymate.ai.dto.PredictionResponse;
import com.inventorymate.ai.service.PredictionService;
import com.inventorymate.ai.service.ProductPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/InventoryMate/v1/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> getPrediction(@RequestParam Long storeId,
                                                            @RequestParam int predictionDays) {
        // Llamar al servicio para obtener la predicción de varios productos
        PredictionResponse predictionResponse = predictionService.getPrediction(storeId, predictionDays);
        return ResponseEntity.ok(predictionResponse);
    }
}