package com.inventotymate.business.controller;

import com.inventotymate.business.model.Product;
import com.inventotymate.business.repository.ProductRepository;
import com.inventotymate.business.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    // URL: http://localhost:8081/api/InventoryMate/v1/products
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

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: GET
    // Description: Get product by id
    @Transactional(readOnly = true)
    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId) {
        if(productRepository.existsById(productId)) {
            return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product
    // Method: POST
    // Description: Save product
    @Transactional
    @PostMapping("/product")
    public ResponseEntity<Product> saveProduct(@RequestBody Product newProduct) {
        Product product = new Product();
        product.setProductName(newProduct.getProductName());
        product.setProductDescription(newProduct.getProductDescription());
        product.setProductPrice(newProduct.getProductPrice());
        product.setCategory(newProduct.getCategory());
        product.setHasExpiration(newProduct.isHasExpiration());
        if (newProduct.isHasExpiration()){
            product.setExpirationDate(newProduct.getExpirationDate());
        }

        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: PUT
    // Description: Update product
    @Transactional
    @PutMapping("/product/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable(name = "productId") Long productId, @RequestBody Product updatedProduct) {
        if(productRepository.existsById(productId)) {
            updatedProduct.setId(productId);
            return new ResponseEntity<>(productService.updateProduct(updatedProduct), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: DELETE
    // Description: Delete product
    @Transactional
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "productId") Long productId) {
        if(productRepository.existsById(productId)) {
            productService.deleteProduct(productId);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/category/{categoryId}
    // Method: GET
    // Description: Get products by category
    @GetMapping("/products/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable(name = "categoryId") Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products/exists
    // Method: GET
    // Description: Check if product exists
    @GetMapping("/product/exists")
    public ResponseEntity<Boolean> checkProductExists(@RequestParam String name) {
        boolean exists = productService.existsByProductName(name);
        return ResponseEntity.ok(exists);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{id}/is-expired
    // Method: GET
    // Description: Check if product is expired
    @GetMapping("/product/{id}/is-expired")
    public boolean isProductExpired(@PathVariable(name = "id") Long id) {
        return productService.isProductExpired(id);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products/expired
    // Method: GET
    // Description: Get expired products
    @GetMapping("/products/expired")
    public List<Product> getExpiredProducts() {
        return productService.getExpiredProducts();
    }

    @PutMapping("/product/{id}/expiration")
    public ResponseEntity<Product> updateExpirationDate(
            @PathVariable(name = "id") Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date newDate) {
        Product updatedProduct = productService.updateExpirationDate(id, newDate);
        return updatedProduct != null ? ResponseEntity.ok(updatedProduct) : ResponseEntity.notFound().build();
    }
}
