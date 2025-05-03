package com.inventorymate.business.service.impl;

import com.inventorymate.business.dto.ProductRequest;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.repository.CategoryRepository;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.repository.StockRepository;
import com.inventorymate.business.service.ProductService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.Store;
import com.inventorymate.user.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final StoreRepository storeRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              StockRepository stockRepository,
                              StoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.stockRepository = stockRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    public List<Product> getAllProducts(Long storeId) {
        return productRepository.findByStore_Id(storeId);
    }

    @Override
    public Product getProductById(Long productId, Long storeId) {
        return productRepository.findByIdAndStore_Id(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " not found in this store"));
    }

    @Override
    @Transactional
    public Product saveProduct(ProductRequest product, Long storeId) {
        return createOrUpdateProduct(product, new Product(), null, storeId);
    }

    @Override
    @Transactional
    public Product updateProduct(ProductRequest productRequest, Long productId, Long storeId) {
        Product productToUpdate = getProductById(productId, storeId);
        return createOrUpdateProduct(productRequest, productToUpdate, productId, storeId);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId, Long storeId) {
        Product product = productRepository.findByIdAndStore_Id(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productRepository.delete(product);
    }

    private Product createOrUpdateProduct(ProductRequest productRequest, Product product, Long productId, Long storeId) {
        if (productRequest.getCategoryId() == 0) {
            product.setCategory(null);
        } else {
            product.setCategory(categoryRepository.findByIdAndStore_Id(productRequest.getCategoryId(), storeId).orElseThrow(
                    () -> new ValidationException("Product category does not exist.")
            ));
        }

        // Validate if unit type is changing
        if (productId != null) { // Only check if it's an update
            Product existingProduct = productRepository.findByIdAndStore_Id(productId, storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (!existingProduct.getUnitType().equals(productRequest.getUnitType())) {
                throw new ValidationException("Cannot change unit type from "
                        + existingProduct.getUnitType() + " to " + productRequest.getUnitType());
            }
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        product.setStore(store);

        product.setProductName(productRequest.getProductName());
        product.setProductDescription(productRequest.getProductDescription());
        product.setProductPrice(productRequest.getProductPrice());
        product.setExpirable(productRequest.isExpirable());
        product.setUnitType(productRequest.getUnitType());

        validateProduct(product, productId, storeId);

        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId, Long storeId) {
        if (categoryId == 0) {
            return productRepository.findByCategoryIsNullAndStore_Id(storeId);
        }
        return productRepository.findByCategoryIdAndStore_Id(categoryId, storeId);
    }

    @Override
    public Long getTotalStockByProductId(Long productId, Long storeId) {
        if (!productRepository.existsByIdAndStore_Id(productId, storeId)) {
            throw new ResourceNotFoundException("Product with ID " + productId + " not found.");
        }

        Long total = stockRepository.getAvailableStockByProductIdAndStoreId(productId, storeId);
        return total != null ? total : 0L;
    }

    @Override
    public boolean existsByProductName(String productName, Long storeId) {
        return productRepository.existsByProductNameIgnoreCaseAndStore_Id(productName, storeId);
    }

    private void validateProduct(Product product, Long productId, Long storeId) {
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new ValidationException("Product name is required.");
        }

        if (product.getUnitType() == null) {
            throw new ValidationException("Unit type cannot be null.");
        }

        if (product.getProductName().length() < 3 || product.getProductName().length() > 50) {
            throw new ValidationException("Product name must be between 3 and 50 characters long.");
        }

        if (!product.getProductName().matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ ]+$")) {
            throw new ValidationException("Product name can only contain letters, numbers, and spaces.");
        }

        // Validate product name uniqueness
        Product existingProduct = productRepository.findByProductNameIgnoreCaseAndStore_Id(product.getProductName(), storeId);
        if (existingProduct != null && (productId == null || !existingProduct.getId().equals(productId))) {
            throw new ValidationException("Product name already exists.");
        }

        if (product.getProductDescription() != null && product.getProductDescription().length() > 100) {
            throw new ValidationException("Product description must not exceed 100 characters.");
        }

        if (product.getProductPrice() <= 0) {
            throw new ValidationException("Product price must be greater than zero.");
        }

        // Validate category
        if (product.getCategory() != null && !categoryRepository.existsByIdAndStore_Id(product.getCategory().getId(), storeId)) {
            throw new ValidationException("Product category does not exist.");
        }
    }
}
