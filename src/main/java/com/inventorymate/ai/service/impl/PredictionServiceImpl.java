package com.inventorymate.ai.service.impl;

import com.inventorymate.ai.component.AzurePredictionClient;
import com.inventorymate.ai.dto.PredictionRequest;
import com.inventorymate.ai.dto.PredictionResponse;
import com.inventorymate.ai.dto.ProductPredictionDetails;
import com.inventorymate.ai.service.PredictionService;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PredictionServiceImpl implements PredictionService {

    private final AzurePredictionClient azurePredictionClient;
    private final ProductRepository productRepository;

    public PredictionServiceImpl(AzurePredictionClient azurePredictionClient,
                                 ProductRepository productRepository) {
        this.azurePredictionClient = azurePredictionClient;
        this.productRepository = productRepository;
    }

    public PredictionResponse getPrediction(Long storeId, int predictionDays) {
        // Calcular la estación y la fecha (puedes usar una librería de fechas para esto)
        String season = calculateSeason();
        String date = LocalDate.now().toString(); // Fecha actual

        List<Product> productsToPredict = productRepository.findByStore_IdAndAssignedForPrediction(storeId, true);

        List<ProductPredictionDetails> productDetailsList = new ArrayList<>();
        for (Product product : productsToPredict) {
            ProductPredictionDetails productDetails = new ProductPredictionDetails();
            productDetails.setProductId(product.getId());
            productDetails.setWeeklySales(product.getWeeklySalesEstimation());
            productDetailsList.add(productDetails);
        }

        // Crear el request de predicción con la lista de productos, estación y fecha
        PredictionRequest predictionRequest = new PredictionRequest();
        predictionRequest.setStoreId(storeId);
        predictionRequest.setPredictionDays(predictionDays);
        predictionRequest.setSeason(season);
        predictionRequest.setDate(date);  // Puedes agregar la fecha si es relevante

        predictionRequest.setProducts(productDetailsList);

        // Realizar la solicitud al cliente de Azure para obtener la predicción
        return azurePredictionClient.fetchPredictionFromAzure(predictionRequest);
    }

    private String calculateSeason() {
        // Lógica para determinar la estación del año basado en la fecha actual
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();

        if (month >= 3 && month <= 5) return "Autumn";
        if (month >= 6 && month <= 8) return "Winter";
        if (month >= 9 && month <= 11) return "Spring";
        return "Summer";
    }
}