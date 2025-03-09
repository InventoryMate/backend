package com.inventotymate.business.service;

import com.inventotymate.business.model.Product;

import java.util.Date;
import java.util.List;

public interface ProductService {
    public List<Product> getAllProducts();
    public Product getProductById(Long productId);
    public Product saveProduct(Product product);
    public Product updateProduct(Product product);
    public void deleteProduct(Long productId);
    public boolean existsByProductName(String productName);
    public List<Product> getProductsByCategory(Long categoryId);
    public boolean isProductExpired(Long productId);
    public List<Product> getExpiredProducts();
    public Product updateExpirationDate(Long productId, Date newExpirationDate);
}
