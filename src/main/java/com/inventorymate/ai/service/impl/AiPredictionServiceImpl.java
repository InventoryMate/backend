package com.inventorymate.ai.service.impl;

import com.inventorymate.ai.dto.PredictionRequest;
import com.inventorymate.ai.dto.PredictionResponse;
import com.inventorymate.ai.service.AiPredictionService;
import com.inventorymate.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiPredictionServiceImpl implements AiPredictionService {

    private final RestTemplate restTemplate;

    @Value("${azure.ai.endpoint}")
    private String aiEndpoint;

    public AiPredictionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PredictionResponse getPrediction(PredictionRequest request) {
        // Validación del campo season (1 a 4)
        if (request.getSeason() < 1 || request.getSeason() > 4) {
            throw new ValidationException("The season must be between 1 y 4.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Si tu API de IA requiere autorización, puedes añadir el header correspondiente
        // headers.set("Authorization", "Bearer " + token);

        HttpEntity<PredictionRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<PredictionResponse> response = restTemplate
                .postForEntity(aiEndpoint, entity, PredictionResponse.class);

        return response.getBody();
    }
}
