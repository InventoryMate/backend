package com.inventorymate.business.service.impl;

import com.inventorymate.business.Dto.ProductRequest;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.repository.CategoryRepository;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.service.ProductService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    @Override
    public Product saveProduct(ProductRequest product) {
        return createOrUpdateProduct(product, new Product(), null);
    }

    @Override
    public Product updateProduct(ProductRequest productRequest, Long productId) {
        Product productToUpdate = getProductById(productId); // Ya maneja ResourceNotFoundException
        return createOrUpdateProduct(productRequest, productToUpdate, productId);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
        }
    }

    private Product createOrUpdateProduct(ProductRequest productRequest, Product product, Long productId) {
        if (productRequest.getCategoryId() == 0) {
            product.setCategory(null);
        } else {
            product.setCategory(categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(
                    () -> new ValidationException("Product category does not exist.")
            ));
        }

        // Validate if unit type is changing
        if (productId != null) { // Only check if it's an update
            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (!existingProduct.getUnitType().equals(productRequest.getUnitType())) {
                throw new ValidationException("Cannot change unit type from "
                        + existingProduct.getUnitType() + " to " + productRequest.getUnitType());
            }
        }

        product.setProductName(productRequest.getProductName());
        product.setProductDescription(productRequest.getProductDescription());
        product.setProductPrice(productRequest.getProductPrice());
        product.setExpirable(productRequest.isExpirable());
        product.setUnitType(productRequest.getUnitType()); // This is now safe

        validateProduct(product, productId);

        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        if (categoryId == 0) {
            return productRepository.findByCategoryIsNull();
        }
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public boolean existsByProductName(String productName) {
        return productRepository.existsByProductNameIgnoreCase(productName);
    }

    private void validateProduct(Product product, Long productId) {
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
        Product existingProduct = productRepository.findByProductNameIgnoreCase(product.getProductName());
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
        if (product.getCategory() != null && !categoryRepository.existsById(product.getCategory().getId())) {
            throw new ValidationException("Product category does not exist.");
        }
    }
}
