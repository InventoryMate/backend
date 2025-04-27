package com.inventorymate.ai.service.impl;

import com.inventorymate.ai.service.ProductPredictionService;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductPredictionServiceImpl implements ProductPredictionService {
    private final ProductRepository productRepository;

    public ProductPredictionServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public void assignProductForPrediction(Long productId, Long storeId, double weeklySales) {
        Product product = productRepository.findByIdAndStore_Id(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setWeeklySalesEstimation(weeklySales);
        product.setAssignedForPrediction(true);
        productRepository.save(product);
    }

    @Transactional
    @Override
    public void unassignProductForPrediction(Long productId, Long storeId) {
        Product product = productRepository.findByIdAndStore_Id(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setAssignedForPrediction(false);
        productRepository.save(product);
    }
}
