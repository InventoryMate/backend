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
import java.util.stream.Collectors;

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
        String season = calculateSeason();
        String date = LocalDate.now().toString();

        System.out.println("Store Id: " + storeId);
        System.out.println("Prediction Days : " + predictionDays);
        List<Product> productsToPredict = productRepository.findByStore_IdAndAssignedForPrediction(storeId, true);

        System.out.println("Luego de products To Predict");

        List<ProductPredictionDetails> productDetailsList = new ArrayList<>();
        for (Product product : productsToPredict) {
            ProductPredictionDetails productDetails = new ProductPredictionDetails();
            productDetails.setProductName(product.getProductName());
            productDetails.setWeeklySales(product.getWeeklySalesEstimation());
            productDetails.setPrice(product.getProductPrice());
            productDetails.setCategoryId(product.getCategory().getId()); // ⚠️ Asegúrate de que Category no sea NULL!!!

            productDetailsList.add(productDetails);
        }

        System.out.println("Luego de products productDetailsList");

        PredictionRequest predictionRequest = new PredictionRequest();
        predictionRequest.setStoreId(storeId);
        predictionRequest.setPredictionDays(predictionDays);
        predictionRequest.setSeason(season);
        predictionRequest.setDate(date);
        predictionRequest.setProducts(productDetailsList);

        System.out.println("Luego de products predictionRequest");

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