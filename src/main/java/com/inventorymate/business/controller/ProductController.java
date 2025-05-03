package com.inventorymate.business.controller;

import com.inventorymate.business.dto.ProductRequest;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.service.ProductService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<Product>> getAllProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Product> products = productService.getAllProducts(userDetails.getStoreId());
        return products.isEmpty()? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: GET
    // Description: Get product by id
    @Transactional(readOnly = true)
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable(name = "productId") Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId, userDetails.getStoreId()));
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product
    // Method: POST
    // Description: Save product
    @Transactional
    @PostMapping
    public ResponseEntity<Product> saveProduct(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ProductRequest newProduct) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(newProduct, userDetails.getStoreId()));
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: PUT
    // Description: Update product
    @Transactional
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "productId") Long productId, @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(productRequest, productId, userDetails.getStoreId()));
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/product/{productId}
    // Method: DELETE
    // Description: Delete product
    @Transactional
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "productId") Long productId) {
        productService.deleteProduct(productId, userDetails.getStoreId());
        return ResponseEntity.noContent().build();
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products/category/{categoryId}
    // Method: GET
    // Description: Get products by category
    @Transactional(readOnly = true)
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "categoryId") Long categoryId) {
        return productService.getProductsByCategory(categoryId, userDetails.getStoreId());
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products/exists
    // Method: GET
    // Description: Check if product exists
    @Transactional(readOnly = true)
    @GetMapping("exists")
    public ResponseEntity<Boolean> checkProductExists(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String name) {
        return ResponseEntity.ok(productService.existsByProductName(name, userDetails.getStoreId()));
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/products/{productId}/stocks-total
    // Method: GET
    // Description: Get total stock by product id
    @Transactional(readOnly = true)
    @GetMapping("/{productId}/stocks-total")
    public ResponseEntity<Long> getTotalStockByProductId(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "productId") Long productId) {
        return ResponseEntity.ok(productService.getTotalStockByProductId(productId, userDetails.getStoreId()));
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
