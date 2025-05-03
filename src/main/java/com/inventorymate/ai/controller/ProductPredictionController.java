package com.inventorymate.ai.controller;

import com.inventorymate.ai.service.ProductPredictionService;
import com.inventorymate.user.model.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/InventoryMate/v1/predictions")
public class ProductPredictionController {

    private final ProductPredictionService productPredictionService;

    public ProductPredictionController(ProductPredictionService productPredictionService) {
        this.productPredictionService = productPredictionService;
    }

    // Asignar un producto para predicción
    @PostMapping("/{productId}/assign")
    public ResponseEntity<String> assignProductForPrediction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam double weeklySales
    ) {
        productPredictionService.assignProductForPrediction(productId, userDetails.getStoreId(), weeklySales);
        return ResponseEntity.ok("Product assigned for prediction successfully.");
    }

    // Desasignar un producto de predicción
    @PostMapping("/{productId}/unassign")
    public ResponseEntity<String> unassignProductForPrediction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId
    ) {
        productPredictionService.unassignProductForPrediction(productId, userDetails.getStoreId());
        return ResponseEntity.ok("Product unassigned from prediction successfully.");
    }
}
