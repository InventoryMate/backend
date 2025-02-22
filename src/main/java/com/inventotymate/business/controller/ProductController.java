package com.inventotymate.business.controller;

import com.inventotymate.business.model.Product;
import com.inventotymate.business.repository.ProductRepository;
import com.inventotymate.business.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/InventoryMate/v1")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    // URL: http://localhost:8080/api/InventoryMate/v1/products
    // Method: GET
    // Description: Get all products
    @Transactional(readOnly = true)
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if(products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // URL: http://localhost:8080/api/InventoryMate/v1/product/{productId}
    // Method: GET
    // Description: Get product by id
    @Transactional(readOnly = true)
    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProductById(Long productId) {
        if(productRepository.existsById(productId)) {
            return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // URL: http://localhost:8080/api/InventoryMate/v1/product/{productId}
    // Method: POST
    // Description: Save product
    @Transactional
    @PostMapping("/product")
    public ResponseEntity<Product> saveProduct(@RequestBody Product newProduct) {
        Product product = new Product();
        product.setProductName(newProduct.getProductName());
        product.setProductDescription(newProduct.getProductDescription());
        product.setProductPrice(newProduct.getProductPrice());
        product.setCategoryId(newProduct.getCategoryId());
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // URL: http://localhost:8080/api/InventoryMate/v1/product/{productId}
    // Method: PUT
    // Description: Update product
    @Transactional
    @PutMapping("/product/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product updatedProduct) {
        if(productRepository.existsById(productId)) {
            return new ResponseEntity<>(productService.updateProduct(updatedProduct), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // URL: http://localhost:8080/api/InventoryMate/v1/product/{productId}
    // Method: DELETE
    // Description: Delete product
    @Transactional
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        if(productRepository.existsById(productId)) {
            productService.deleteProduct(productId);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
