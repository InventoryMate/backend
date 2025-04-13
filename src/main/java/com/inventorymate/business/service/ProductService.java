package com.inventorymate.business.service;

import com.inventorymate.business.Dto.ProductRequest;
import com.inventorymate.business.model.Product;

import java.util.List;

public interface ProductService {
    public List<Product> getAllProducts();
    public Product getProductById(Long productId);
    public Product saveProduct(ProductRequest product);
    public Product updateProduct(ProductRequest product, Long productId);
    public void deleteProduct(Long productId);
    public boolean existsByProductName(String productName);
    public List<Product> getProductsByCategory(Long categoryId);
    public Long getTotalStockByProductId(Long productId);
}
