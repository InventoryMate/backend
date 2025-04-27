package com.inventorymate.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class PredictionRequest {
    private List<ProductPredictionDetails> products; // Lista de productos para la predicción
    private Long storeId;
    private int predictionDays;  // Días a predecir
    private String season;       // Estación del año, si es necesario
    private String date;        // Fecha de la predicción, si es necesario
}