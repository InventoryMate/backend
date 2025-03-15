package com.inventorymate.business.service.impl;

import com.inventorymate.business.model.Product;
import com.inventorymate.business.repository.CategoryRepository;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public Product saveProduct(Product product) {
        if (categoryRepository.existsById(product.getCategory().getId()) || product.getCategory().getId() == 0) {
            return productRepository.save(product);
        }
        throw new IllegalArgumentException("Invalid category ID: " + product.getCategory().getId());
    }

    // Agregar métodos el aspecto de STOCK
    @Override
    public Product updateProduct(Product product) {
        Product productToUpdate = productRepository.findById(product.getId()).orElse(null);
        if (productToUpdate != null) {
            productToUpdate.setProductName(product.getProductName());
            productToUpdate.setProductDescription(product.getProductDescription());
            productToUpdate.setProductPrice(product.getProductPrice());
            productToUpdate.setCategory(product.getCategory());
            productToUpdate.setExpirable(product.isExpirable());
            /*if(product.isHasExpiration()) {
                productToUpdate.setExpirationDate(product.getExpirationDate());
            }*/

            return productRepository.save(productToUpdate);
        }
        else {
            return null;
        }
    }

    @Override
    public void deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
        }
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public boolean existsByProductName(String productName) {
        return productRepository.existsByProductNameIgnoreCase(productName);
    }

    // Agregar métodos el aspecto de STOCK
    /*
    @Override
    public boolean isProductExpired(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        return product != null && product.isExpired();
    }


    @Override
    public List<Product> getExpiredProducts() {
        Date today = new Date();
        return productRepository.findByHasExpirationTrueAndExpirationDateBefore(today);
    }


    // Agregar métodos el aspecto de STOCK

    @Override
    public Product updateExpirationDate(Long productId, Date newExpirationDate) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null && product.isHasExpiration()) {
            product.setExpirationDate(newExpirationDate);
            return productRepository.save(product);
        }
        return null;
    }
    */
}
