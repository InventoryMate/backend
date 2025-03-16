package com.inventorymate.business.controller;

import com.inventorymate.business.Dto.ProductRequest;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.model.Stock;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.service.ProductService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//CHANGE REQUEST MAPPING TO /api/InventoryMate/v1
@RestController
@RequestMapping("/api/InventoryMate/v1/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products
    // Method: GET
    // Description: Get all products
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return products.isEmpty()? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: GET
    // Description: Get product by id
    @Transactional(readOnly = true)
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product
    // Method: POST
    // Description: Save product
    @Transactional
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody ProductRequest newProduct) {
        Product savedProduct =productService.saveProduct(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: PUT
    // Description: Update product
    @Transactional
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable(name = "productId") Long productId, @RequestBody ProductRequest productRequest) {
        Product product = productService.updateProduct(productRequest, productId);
        return ResponseEntity.ok(product);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: DELETE
    // Description: Delete product
    @Transactional
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products/category/{categoryId}
    // Method: GET
    // Description: Get products by category
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable(name = "categoryId") Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products/exists
    // Method: GET
    // Description: Check if product exists
    @GetMapping("exists")
    public ResponseEntity<Boolean> checkProductExists(@RequestParam String name) {
        boolean exists = productService.existsByProductName(name);
        return ResponseEntity.ok(exists);
    }

    // Global Exception Handling for Not Found & Validation Exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
