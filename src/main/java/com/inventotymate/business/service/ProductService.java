package com.inventotymate.business.service;

import com.inventotymate.business.model.Product;

import java.util.List;

public interface ProductService {
    public List<Product> getAllProducts();
    public Product getProductById(Long productId);
    public Product saveProduct(Product product);
    public Product updateProduct(Product product);
    public void deleteProduct(Long productId);
}
