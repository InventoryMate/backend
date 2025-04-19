package com.inventorymate.business.controller;

import com.inventorymate.business.dto.CategoryRequest;
import com.inventorymate.business.model.Category;
import com.inventorymate.business.service.CategoryService;
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

@RestController
@RequestMapping("/api/InventoryMate/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/categories
    // Method: GET
    // Description: Get all categories
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Category> categories = categoryService.getAllCategories(userDetails.getStoreId());
        return categories.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(categories);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/category/{categoryId}
    // Method: GET
    // Description: Get category by id
    @Transactional(readOnly = true)
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable(name = "categoryId") Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId, userDetails.getStoreId());
        return ResponseEntity.ok(category);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/categories
    // Method: POST
    // Description: Save category
    @Transactional
    @PostMapping
    public ResponseEntity<Category>createCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestBody CategoryRequest category) {
        System.out.println("User details: " + userDetails);
        System.out.println("getStoreId " + userDetails.getStoreId());
        Category savedCategory = categoryService.saveCategory(category, userDetails.getStoreId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/category/{categoryId}
    // Method: PUT
    // Description: Update category
    @Transactional
    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable(name = "categoryId") Long categoryId,
                                                   @RequestBody CategoryRequest updatedCategory) {
        Category category = categoryService.updateCategory(updatedCategory, categoryId, userDetails.getStoreId());
        return ResponseEntity.ok(category);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/category/{categoryId}
    // Method: DELETE
    // Description: Delete category
    @Transactional
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @PathVariable(name = "categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId, userDetails.getStoreId());
        return ResponseEntity.noContent().build();
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