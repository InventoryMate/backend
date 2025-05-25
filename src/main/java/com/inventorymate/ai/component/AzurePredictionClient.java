package com.inventorymate.ai.component;

import com.inventorymate.ai.dto.PredictionRequest;
import com.inventorymate.ai.dto.PredictionResponse;
import com.inventorymate.user.model.CustomUserDetails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class AzurePredictionClient {
    private final RestTemplate restTemplate;

    @Value("${azure.ai.endpoint}")
    private String azureEndpoint;

    public AzurePredictionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PredictionResponse fetchPredictionFromAzure(PredictionRequest predictionRequest) {
        HttpEntity<PredictionRequest> requestEntity = new HttpEntity<>(predictionRequest);

        ResponseEntity<PredictionResponse> response = restTemplate.exchange(
                azureEndpoint, HttpMethod.POST, requestEntity, PredictionResponse.class
        );

        return response.getBody();
    }
}