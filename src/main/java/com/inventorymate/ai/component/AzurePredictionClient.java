package com.inventorymate.ai.component;

import com.inventorymate.ai.dto.PredictionRequest;
import com.inventorymate.ai.dto.PredictionResponse;
import com.inventorymate.user.model.CustomUserDetails;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class AzurePredictionClient {

    private final RestTemplate restTemplate;

    public AzurePredictionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Método para hacer la solicitud a Azure con múltiples productos
    public PredictionResponse fetchPredictionFromAzure(PredictionRequest predictionRequest) {
        // La URL de tu endpoint de predicción de Azure (puede ser diferente según tu configuración)
        String url = "https://azure-prediction-endpoint.com/predict";

        HttpEntity<PredictionRequest> requestEntity = new HttpEntity<>(predictionRequest);

        // Realizar la solicitud POST al endpoint de Azure
        ResponseEntity<PredictionResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, PredictionResponse.class);

        return response.getBody();
    }
}
