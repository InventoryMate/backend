package com.inventorymate.business.controller;

import com.inventorymate.business.model.Category;
import com.inventorymate.business.repository.CategoryRepository;
import com.inventorymate.business.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//CHANGE REQUEST MAPPING TO /api/InventoryMate/v1/categories
@Controller
@RequestMapping("/api/InventoryMate/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/categories
    // Method: GET
    // Description: Get all categories
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if(categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/category/{categoryId}
    // Method: GET
    // Description: Get category by id
    @Transactional(readOnly = true)
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable(name = "categoryId") Long categoryId) {
        if(categoryRepository.existsById(categoryId)) {
            return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/categories
    // Method: POST
    // Description: Save category
    @Transactional
    @PostMapping
    public ResponseEntity<Category>createCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/category/{categoryId}
    // Method: PUT
    // Description: Update category
    @Transactional
    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable(name = "categoryId") Long categoryId, @RequestBody Category updatedCategory) {
        if(categoryRepository.existsById(categoryId)) {
            updatedCategory.setId(categoryId);
            return new ResponseEntity<>(categoryService.updateCategory(updatedCategory), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/category/{categoryId}
    // Method: DELETE
    // Description: Delete category
    @Transactional
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        if(categoryRepository.existsById(categoryId)) {
            categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>("category deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
