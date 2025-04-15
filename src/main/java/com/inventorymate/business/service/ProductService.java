package com.inventorymate.business.service;

import com.inventorymate.business.Dto.ProductRequest;
import com.inventorymate.business.model.Product;

import java.util.List;

public interface ProductService {
    public List<Product> getAllProducts(Long storeId);
    public Product getProductById(Long productId, Long storeId);
    public Product saveProduct(ProductRequest product, Long storeId);
    public Product updateProduct(ProductRequest product, Long productId, Long storeId);
    public void deleteProduct(Long productId, Long storeId);
    public boolean existsByProductName(String productName, Long storeId);
    public List<Product> getProductsByCategory(Long categoryId, Long storeId);
    public Long getTotalStockByProductId(Long productId, Long storeId);
}
